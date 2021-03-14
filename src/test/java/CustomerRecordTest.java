import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomerRecordTest {
    CustomerRecord customerRecord = new CustomerRecord("src/main/resources/data.json");

    @Test
    void markDuplicateRecordsTest() throws IOException {
        Set<String> records = customerRecord.markDuplicateRecords(customerRecord.fileReader());
        Set<String> uniqueRecords = new HashSet<>(records);

        Assertions.assertEquals(uniqueRecords.toArray().length, records.toArray().length);
    }

    @Test
    void markMissingNameTest() throws IOException {
        List<String> records = customerRecord.markMissingName(customerRecord.fileReader());

        for(String record : records) {
            String[] keyValueName = record.substring(record.indexOf("\""), record.indexOf(","))
                    .split(":");

            boolean nullName = keyValueName[1].equals("null");
            boolean missingName = keyValueName[1].length() <= 2;
            boolean blankName = keyValueName[1].strip().length() == 0;

            Assertions.assertTrue(nullName || missingName || blankName);
            System.out.println("Record with no name: " + record);
        }
        System.out.println("Total records with no name: " + records.toArray().length);
    }

    @Test
    void markMissingAddressTest() throws IOException {
        List<String> records = customerRecord.markMissingAddress(customerRecord.fileReader());
        List<String> containsNoAddress = new ArrayList<>(1000);
        List<String> containsBlankAddress = new ArrayList<>(5000);

        for (String record : records) {
            if (!record.contains("address")) {
                containsNoAddress.add(record);
                System.out.println("Record with no address: " + record);
            }

            else {
                if (record.indexOf("address") <  record.lastIndexOf(",\"")) {
                    String[] splitRecord = record.substring(record.indexOf("address"), record.lastIndexOf(",\"")).split(":");
                    CustomerRecord.checkMissingNullBlank(splitRecord, containsBlankAddress);
                } else {
                    String[] splitRecord = record.substring(record.indexOf("address"), record.lastIndexOf("}")).split(":");
                    CustomerRecord.checkMissingNullBlank(splitRecord, containsBlankAddress);
                }
                System.out.println("Record with missing address: " + record);
            }
        }

        containsNoAddress.forEach(noAddressRecord -> Assertions.assertFalse(noAddressRecord.contains("address")));
        containsBlankAddress.forEach(blankAddressRecord ->
                Assertions.assertTrue(blankAddressRecord.equals("null") || blankAddressRecord.strip().length() <= 2));
    }

    @Test
    void markMissingZipTest() throws IOException {
        List<String> records = customerRecord.markMissingZipCode(customerRecord.fileReader());
        List<String> containsNoZip = new ArrayList<>(1000);
        List<String> containsInvalidZip = new ArrayList<>(1000);

        for (String record : records) {
            if (!record.contains("zip")) {
                containsNoZip.add(record);
            } else {
                String[] splitZipRecord = record.substring(record.indexOf("\"zip"), record.indexOf("}"))
                        .split(":");

                if (CustomerRecord.validUsZip(splitZipRecord[1])) {
                    containsInvalidZip.add(splitZipRecord[1]);
                }
            }
            System.out.println("Record with zip is null, missing, or an invalid U.S. zipcode: " +record);
        }

        containsNoZip.forEach(noZip -> Assertions.assertFalse(noZip.contains("zip")));
        containsInvalidZip.forEach(invalidZip ->
                Assertions.assertTrue(invalidZip.equals("null") || invalidZip.strip().length() != 5));
    }

}
