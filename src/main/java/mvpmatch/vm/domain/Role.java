package mvpmatch.vm.domain;

import java.util.Arrays;

/**
 * Constants for authorities.
 */
public final class Role {

    public static final String BUYER = "BUYER";

    public static final String SELLER = "SELLER";

    public static final String[] ALL_ROLES = {BUYER, SELLER};

    public static boolean isValidRole(String role) {
        String upperCaseRole = role.toUpperCase();
        return Arrays.stream(ALL_ROLES).anyMatch(upperCaseRole::equals);
    }
}
