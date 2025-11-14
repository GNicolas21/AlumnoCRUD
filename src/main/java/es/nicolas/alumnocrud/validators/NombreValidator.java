package es.nicolas.alumnocrud.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NombreValidator implements ConstraintValidator<Nombre, String> {
    @Override
    public boolean isValid(String nombreField, ConstraintValidatorContext context) {
        if (nombreField == null){
            return true; // Permite valores nulos
        }
        boolean valid30length = nombreField.matches("^.{1,30}$");
        boolean validCharacters = nombreField.matches("^[A-Za-zÁÉÍÓÚáéíóúÑñÜü\\s]+$");
        return valid30length || validCharacters;
    }

    @Override
    public void initialize(Nombre nombre){}
}
