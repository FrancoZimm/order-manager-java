# Order Manager · Java

[![CI](https://github.com/FrancoZimm/order-manager-java/actions/workflows/maven.yml/badge.svg)](https://github.com/FrancoZimm/order-manager-java/actions/workflows/maven.yml)

Aplicación de escritorio en **Java (Swing)** para gestionar pedidos: buscarlos, crearlos, editarlos y borrarlos, con los importes en **euros o dólares** según el tipo de cambio en tiempo real.

La desarrollé en la asignatura *Introducción a la Ingeniería de Software* del Grado en Ingeniería Informática (Universidad Europea de Madrid), aplicando arquitectura **MVC**, pruebas unitarias e integración continua.

---

## Funcionalidades

- **Lista de pedidos** en un panel lateral; al seleccionar uno se rellena la búsqueda.
- **Búsqueda** por ID con el detalle de artículos, total bruto y total con descuento.
- **Crear pedidos** desde un formulario propio, con validaciones: ID obligatorio y único, y al menos un artículo.
- **Editar** la cantidad y el descuento de cada artículo.
- **Borrar** pedidos, con confirmación.
- **EUR / USD**: el tipo de cambio se obtiene de una API pública ([open.er-api.com](https://open.er-api.com)). Si la API falla, la app avisa y sigue en euros.
- **Persistencia en JSON** (`orders.json`): cada cambio se guarda y la lectura tolera campos de versiones anteriores.

---

## Arquitectura

```text
com.example
├── model/        Order, Article, Calculator, Searcher
├── view/         OrderView, OrderCreationDialog, OrderEditDialog   (Swing)
├── controller/   OrderController   → decide qué hacer y coordina
├── service/      ExchangeRateService → tipo de cambio EUR→USD (OkHttp + Jackson)
└── Main.java
```

- **La vista** solo muestra, **el controlador** decide y **el servicio** obtiene el dato externo.
- Diagramas **PlantUML** en `src/main/plantuml/`: clases, casos de uso, secuencia y despliegue.

---

## Stack

`Java 17` · `Swing` · `Maven` · `Jackson` · `OkHttp` · `SLF4J + Logback` · `JUnit 5` · `GitHub Actions` · `PlantUML`

---

## Cómo ejecutarlo

Requisitos: **JDK 17+** y **Maven**.

```bash
git clone https://github.com/FrancoZimm/order-manager-java.git
cd order-manager-java
mvn clean package
mvn exec:java -Dexec.mainClass=com.example.Main
```

Pruebas:

```bash
mvn test
```

---

## Integración continua

El workflow de GitHub Actions (`.github/workflows/maven.yml`) compila y ejecuta las pruebas en cada push a `main`. Si cambia la versión del `pom.xml`, publica una nueva release automáticamente.

---

Hecho por **Franco Zimmermann** · [@FrancoZimm](https://github.com/FrancoZimm)
