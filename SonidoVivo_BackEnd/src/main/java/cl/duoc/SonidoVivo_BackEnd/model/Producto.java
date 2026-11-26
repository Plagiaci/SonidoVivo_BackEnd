package cl.duoc.SonidoVivo_BackEnd.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

@Table(name = "productos")
public class Producto {
    @Id
    @Column(name = "id_producto", length = 20)
    @NotBlank(message = "El ID del producto es obligatorio")
    private String id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "La marca es obligatoria")
    @Column(nullable = false, length = 50)
    private String marca;

    @NotBlank(message = "El modelo es obligatorio")
    @Column(nullable = false, length = 50)
    private String modelo;

    @NotNull(message = "El stock es obligatorio")
    @PositiveOrZero(message = "El stock no puede ser negativo")
    @Column(nullable = false)
    private Integer stock;

    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor que 0")
    @Column(nullable = false)
    private Integer precio;

    @Size(
            max = 500,
            message = "La descripción no puede superar 500 caracteres"
    )
    @Column(length = 500)
    private String descripcion;

    @NotNull(message = "La categoría es obligatoria")
    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;
}
