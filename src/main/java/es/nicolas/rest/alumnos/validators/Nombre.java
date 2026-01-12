package es.nicolas.rest.alumnos.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NombreValidator.class)
@Target( {ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Nombre {
    String message() default "Nombre no válido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
