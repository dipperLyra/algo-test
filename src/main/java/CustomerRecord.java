import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Eche Mkpadi
 * Algorithm test.
 * Question:
 *    Attached is an imaginary example set of data a customer might need to
 *    migrate from one system to another. It's a JSON encoded array of objects.
 *    The customer understands some of the data might be bad and want to know
 *    which records are invalid so they can ensure the new system will only have
 *    valid data. Write a program that will read in the data and mark any
 *    record:
 *    1. That are a duplicate of another record
 *    2. `name` field is null, missing, or blank
 *    3. `address` field is null, missing, or blank
 *    4. `zip` is null, missing, or an invalid U.S. zipcode
 */

public class CustomerRecord {

    private final Path path ;

    public CustomerRecord(String path) {
        this.path = Paths.get(path);
    }

    /**
     * <p>This method checks for duplicates in a list of records and returns only unique records</p>
     * <p>Takes a list of json formatted records, uses regex (method patternMatcher) to break it into groups of record.
     * For example,
     * [{"name":"Jenasys Massey","address":"2755 Knight Street","zip":"32126"},{"name":"Germaine Burkhart","address":"5867 Cottage Street","zip":"56531"}]
     * will be broken into,
     * group1 => {"name":"Jenasys Massey","address":"2755 Knight Street","zip":"32126"} and
     * group2 => {"name":"Germaine Burkhart","address":"5867 Cottage Street","zip":"56531"}</p>
     *
     * <p>Each group of record is put inside a Set. Set contains only unique objects,
     * so, putting it inside a Set enforces that only unique objects are returned.</p>
     *
     * @param recordList list of records to be checked for duplicates
     * @return list of unique records
     */
    public Set<String> markDuplicateRecords(List<String> recordList) {
        Set<String> uniqueRecords = new HashSet<>(10000);
        List<String> groups =  new ArrayList<>(10000);

        Matcher recordMatcher = patternMatcher(recordList);

        while (recordMatcher.find()) {
            if (!groups.contains(recordMatcher.group())) {
                uniqueRecords.add(recordMatcher.group());
            }
            groups.add(recordMatcher.group());
        }
        return uniqueRecords;
    }

    /**
     * <p>This method checks record for where name field is null, missing, or blank </p>
     * <p>It takes a list of records checks the name fields and
     * returns a list of records that have null, missing or blank name field.</p>
     * @param recordList list of records to check for null, missing or blank name field.
     * @return list of records that have null, missing or blank name field.
     */
    public List<String> markMissingName(List<String> recordList) {
        Matcher recordMatcher = patternMatcher(recordList);
        List<String> emptyNameRecords = new ArrayList<>(10000);

        while (recordMatcher.find()) {
            String[] keyValueName = recordMatcher.group()
                    .substring(recordMatcher.group().indexOf("\""), recordMatcher.group().indexOf(","))
                    .split(":");

            checkMissingNullBlank(keyValueName, recordMatcher.group(), emptyNameRecords);
        }
        return emptyNameRecords;
    }

    /**
     * <p>This method checks for `address` field is null, missing, or blank</p>
     * @param recordList list of record to check for `address` field is null, missing, or blank
     * @return list of records that have `address` field is null, missing, or blank
     */
    public List<String> markMissingAddress(List<String> recordList) {
        Matcher recordMatcher = patternMatcher(recordList);
        List<String> emptyAddressRecord = new ArrayList<>(10000);

        while(recordMatcher.find()) {
            String group = recordMatcher.group();
            int indexOfAddress = recordMatcher.group().indexOf("\"address");
            String[] keyValuePair;

            if (!group.contains("address")) {
                emptyAddressRecord.add(group);
            } else {

                if (indexOfAddress < group.lastIndexOf(",\"")) {
                    keyValuePair = group.substring(indexOfAddress, group.lastIndexOf(",\"")).split(":");

                    checkMissingNullBlank(keyValuePair, group, emptyAddressRecord);
                } else {
                    keyValuePair = group.substring(indexOfAddress, group.lastIndexOf("}")).split(":");

                    checkMissingNullBlank(keyValuePair, group, emptyAddressRecord);
                }
            }
        }

        return emptyAddressRecord;
    }

    /**
     * <p>This method checks for field `zip` is null, missing, or an invalid U.S. zipcode</p>
     * @param recordList list of records to check for `zip` is null, missing, or an invalid U.S. zipcode
     * @return list of records with `zip` is null, missing, or an invalid U.S. zipcode
     */
    public List<String> markMissingZipCode(List<String> recordList) {
        Matcher recordMatcher = patternMatcher(recordList);
        List<String> missingAndInvalidZipRecord = new ArrayList<>(10000);

        while (recordMatcher.find()) {
            String group = recordMatcher.group();

            if (!group.contains("zip")) {
                missingAndInvalidZipRecord.add(group);
            } else {
                String[] keyValuePair = recordMatcher.group()
                        .substring(recordMatcher.group().indexOf("\"zip"), recordMatcher.group().indexOf("}"))
                        .split(":");

                if (!validUsZip(keyValuePair[1])) {
                    missingAndInvalidZipRecord.add(group);
                }
            }
        }

        return missingAndInvalidZipRecord;
    }

    /**
     * Check if a string is null, missing or blank.
     * Note: it checks for literal null because when the JSON is read in the null value is converted to string null
     * instead of Java type null. Something similar goes with "", empty quotes are read in as literal quotes.
     * Hence, the reason for checking for string length is less than two because it will count "" as two
     * instead of an empty string. [These can be made more robust in a future implementation]
     * @param keyValuePair array of String to check for field is null, missing, or an invalid U.S. zipcode
     * @param group the particular record that the field belongs to
     * @param list takes a list to store the record if it is null, missing, or an invalid U.S. zipcode
     */
    public static void checkMissingNullBlank(String[] keyValuePair, String group, List<String> list) {
        if (keyValuePair[1].equals("null") || keyValuePair[1].length() <= 2 || keyValuePair[1].strip().length() == 0) {
            list.add(group);
        }
    }

    public static void checkMissingNullBlank(String[] keyValuePair, List<String> list) {
        if (keyValuePair[1].equals("null") || keyValuePair[1].length() <= 2 || keyValuePair[1].strip().length() == 0) {
            list.add(keyValuePair[1]);
        }
    }

    /**
     * Returns true if the zip supplied is a valid US zip.
     * It uses two simple rules to check. Note, this rule can easily be expanded.
     * 1. It checks that the zip supplied has five characters
     * 2. It checks that the zip supplied contains only numbers(digits).
     * @param zip A zip number to check for validity
     * @return boolean. True if it's a valid US zip and false it's not, following the two rules above.
     */
    public static boolean validUsZip(String zip) {
        if (zip.length() != 7) { return false; }

        if (zip.contains("\"")) {
            String withoutQuotes = zip.substring(zip.indexOf("\"") + 1,  zip.lastIndexOf("\""));

            for (int i = 0;  i < withoutQuotes.length(); i++) {
                boolean isDigit =  Character.isDigit(withoutQuotes.charAt(i));

                if (!isDigit) { return false; }
            }
        }
        return true;
    }

    /**
     * Reads the file which was set in the class constructor and returns its content as a List of String.
     * @return File content as List of String
     * @throws IOException
     */
    public List<String> fileReader() throws IOException {
        List<String> recordList;
        try (Stream<String> records = Files.lines(path)){
            recordList = records.collect(Collectors.toList());
        }
        return recordList;
    }

    /**
     * Takes a list of records, breaks it up with regex and returns the result of the matches
     * @param recordList list of records
     * @return Matcher containing the break of running the specified regex over recordList
     */
    private Matcher patternMatcher(List<String> recordList) {
        Pattern pattern = Pattern.compile("\\{(.*?)}");
        return pattern.matcher(recordList.toString());
    }
}
