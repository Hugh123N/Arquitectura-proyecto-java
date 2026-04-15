package com.soft.reclutamiento.dto.dbo.area;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateAreaDto extends AreaDto {
    // Hereda 'nombre' de AreaDto con validaciones

    @Override
    @NotBlank(message = "{common.field.required}")
    @Size(max = 255, message = "{common.field.max.length}")
    public void setNombre(String nombre) {
        super.setNombre(nombre);
    }
}
