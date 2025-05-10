# language: es
Característica: Gestión de publicaciones de aventuras

  Escenario: Crear una nueva publicación de aventura
    Dado un emprendedor con ID 1
    Y una aventura con título "Aventura en los Andes", descripción "Una increíble aventura en las montañas", capacidad 5 personas y duración 3 horas
    Y un costo de 500 soles
    Y una imagen "https://example.com/image.jpg"
    Cuando el emprendedor crea una nueva publicación
    Entonces la publicación se guarda correctamente con ID 1
    Y la publicación contiene la información correcta de la aventura
    Y la publicación tiene el costo correcto
    Y la publicación muestra la imagen correcta

  Escenario: Buscar una publicación por ID
    Dado que existe una publicación con ID 1
    Cuando el usuario busca la publicación con ID 1
    Entonces se muestra la publicación correcta
    Y la publicación tiene título "Aventura en los Andes"

  Escenario: Listar todas las publicaciones
    Dado que existen múltiples publicaciones en el sistema
    Cuando el usuario solicita ver todas las publicaciones
    Entonces se muestra la lista completa de publicaciones
    Y la lista contiene al menos 2 publicaciones 