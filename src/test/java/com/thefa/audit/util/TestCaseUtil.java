package com.thefa.audit.util;

import lombok.extern.apachecommons.CommonsLog;

import java.time.LocalDate;

@CommonsLog
public class TestCaseUtil {

    public static String editPlayersJsonWithPlayerIds(String playerId1, String playerId2) {
        return "[\n" +
                "\t{\n" +
                "\t\t\"playerId\": \"" + playerId1 + "\",\n" +
                "\t\t\"maturationStatus\": \"EARLY\",\n" +
                "\t\t\"maturationDate\": \"2018-12-31\",\n" +
                "\t\t\"vulnerabilityStatus\": 1,\n" +
                "\t\t\"vulnerabilityDate\": \"2018-12-31\"\n" +
                "\t},{\n" +
                "\t\t\"playerId\": \"" + playerId2 + "\",\n" +
                "\t\t\"maturationStatus\": \"EARLY\",\n" +
                "\t\t\"maturationDate\": \"2018-12-31\",\n" +
                "\t\t\"vulnerabilityStatus\": 2,\n" +
                "\t\t\"vulnerabilityDate\": \"2018-12-31\"\n" +
                "\t}\n" +
                "]";
    }


    public static String editPlayersJsonWithPlayerIdsAndDates(String validPlayerId, LocalDate murationDate, LocalDate vulnerabilityDate,
                                                              String invalidPlayerId) {
        return "[\n" +
                "\t{\n" +
                "\t\t\"playerId\": \"" + validPlayerId + "\",\n" +
                "\t\t\"maturationStatus\": \"EARLY\",\n" +
                "\t\t\"maturationDate\": \"" + murationDate + "\",\n" +
                "\t\t\"vulnerabilityStatus\": 1,\n" +
                "\t\t\"vulnerabilityDate\": \"" + vulnerabilityDate + "\"\n" +
                "\t},{\n" +
                "\t\t\"playerId\": \"" + invalidPlayerId + "\",\n" +
                "\t\t\"maturationStatus\": \"EARLY\",\n" +
                "\t\t\"maturationDate\": \"" + murationDate + "\",\n" +
                "\t\t\"vulnerabilityStatus\": 2,\n" +
                "\t\t\"vulnerabilityDate\": \"" + vulnerabilityDate + "\"\n" +
                "\t}\n" +
                "]";
    }

    public static String editPlayersJsonWithPlayerIdsAndEmptyData(String validPlayerId1, LocalDate murationDate, LocalDate vulnerabilityDate, String validPlayerId2) {
        return "[\n" +
                "\t{\n" +
                "\t\t\"playerId\": \"" + validPlayerId1 + "\",\n" +
                "\t\t\"maturationStatus\": \"EARLY\",\n" +
                "\t\t\"maturationDate\": \"" + murationDate + "\",\n" +
                "\t\t\"vulnerabilityStatus\": 1,\n" +
                "\t\t\"vulnerabilityDate\": \"" + vulnerabilityDate + "\"\n" +
                "\t},{\n" +
                "\t\t\"playerId\": \"" + validPlayerId2 + "\",\n" +
                "\t\t\"maturationStatus\": \"EARLY\",\n" +
                "\t\t\"vulnerabilityStatus\": 2\n" +
                "\t}\n" +
                "]";

    }

    public static String editPlayersJsonWithPlayerIdsAndSpacificData(String validPlayerId1, LocalDate murationDate, LocalDate vulnerabilityDate, String validPlayerId2) {
        return "[\n" +
                "\t{\n" +
                "\t\t\"playerId\": \"" + validPlayerId1 + "\",\n" +
                "\t\t\"maturationStatus\": \"EARLY\",\n" +
                "\t\t\"maturationDate\": \"" + murationDate + "\",\n" +
                "\t\t\"vulnerabilityStatus\": 1\n" +
                "\t},{\n" +
                "\t\t\"playerId\": \"" + validPlayerId2 + "\",\n" +
                "\t\t\"maturationStatus\": \"EARLY\",\n" +
                "\t\t\"vulnerabilityStatus\": 1,\n" +
                "\t\t\"vulnerabilityDate\": \"" + vulnerabilityDate + "\"\n" +
                "\t}\n" +
                "]";

    }

    public static String editPlayersJsonWithNonEmptyData(String validPlayerId1, LocalDate murationDate, LocalDate vulnerabilityDate, String validPlayerId2) {
        return "[\n" +
                "\t{\n" +
                "\t\t\"playerId\": \"" + validPlayerId1 + "\",\n" +
                "\t\t\"maturationStatus\": \"EARLY\",\n" +
                "\t\t\"maturationDate\": \"" + murationDate + "\",\n" +
                "\t\t\"vulnerabilityStatus\": 1\n" +
                "\t},{\n" +
                "\t\t\"playerId\": \"" + validPlayerId2 + "\"\n" +
                "\t}\n" +
                "]";

    }

    public static String editPlayersJsonWithEmptyData(String validPlayerId1) {
        return "[\n" +
                "\t{\n" +
                "\t\t\"playerId\": \"" + validPlayerId1 + "\",\n" +
                "\t\t\"maturationStatus\": \"EARLY\",\n" +
                "\t\t\"vulnerabilityStatus\": 1\n" +
                "\t},{\n" +
                "\t}\n" +
                "]";

    }
}
