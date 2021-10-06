package mvpmatch.vm.config;


import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "vending-machine")
public interface ApplicationProperties {
    Security security();

    interface Security {
        Authentication authentication();

        interface Authentication {
            Jwt jwt();

            interface Jwt {
                String issuer();
                long tokenValidityInSeconds();
                PrivateKey privateKey();

                interface PrivateKey {
                    String location();
                }
            }
        }
    }

}
