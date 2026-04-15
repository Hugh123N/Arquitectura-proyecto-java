package com.soft.reclutamiento.dto.dbo.area;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAreaDto extends AreaDto {

    @NotNull(message = "{common.identifier.required}")
    private Integer idArea;

    @Override
    @NotBlank(message = "{common.field.required}")
    @Size(max = 255, message = "{common.field.max.length}")
    public void setNombre(String nombre) {
        super.setNombre(nombre);
    }
}
