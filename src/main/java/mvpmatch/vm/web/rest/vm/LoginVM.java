package mvpmatch.vm.web.rest.vm;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@RegisterForReflection
@Data
public class LoginVM {
    @NotNull
    @Size(min = 1, max = 45)
    private String username;

    @NotNull
    @Size(min = 1, max = 100)
    private String password;
}
