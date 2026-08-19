package ph.edu.dlsu.lbycpob.animoquest.util;

public class IDValidator {

    private IDValidator() {
        // Utility class; no objects needed.
    }

    public static boolean validateID(String id) {

        if (id == null || id.length() != 8) {
            return false;
        }

        int[] weights = {8, 7, 6, 5, 4, 3, 2, 1};
        int sum = 0;

        for (int i = 0; i < 8; i++) {

            char c = id.charAt(i);

            if (!Character.isDigit(c)) {
                return false;
            }

            int digit = c - '0';
            sum += digit * weights[i];
        }

        return sum % 11 == 0;
    }
}