# 📚 Fundamentos de Programación - Desde Cero

Guía completa de conceptos básicos de programación para entender cualquier lenguaje.

---

## 📋 Tabla de Contenidos

1. [¿Qué es la Programación?](#qué-es-la-programación)
2. [Conceptos Básicos Fundamentales](#conceptos-básicos-fundamentales)
3. [Variables y Tipos de Datos](#variables-y-tipos-de-datos)
4. [Operadores](#operadores)
5. [Estructuras de Control](#estructuras-de-control)
6. [Funciones y Métodos](#funciones-y-métodos)
7. [Estructuras de Datos](#estructuras-de-datos)
8. [Programación Orientada a Objetos](#programación-orientada-a-objetos)
9. [Manejo de Errores](#manejo-de-errores)
10. [Algoritmos Básicos](#algoritmos-básicos)

---

## 🎯 ¿Qué es la Programación?

### Definición
**Programar** es el proceso de crear instrucciones que una computadora puede ejecutar para realizar tareas específicas. Es como escribir una receta detallada que la computadora sigue al pie de la letra.

### Componentes de un Programa
```
Entrada → Procesamiento → Salida
```

**Ejemplo Real**:
- **Entrada**: Usuario escribe nombre y edad
- **Procesamiento**: Programa calcula si es mayor de edad
- **Salida**: Muestra "Eres mayor de edad" o "Eres menor de edad"

### Lenguajes de Programación
Son idiomas que los humanos usan para comunicarse con las computadoras.

**Tipos**:
- **Bajo nivel**: Cercanos al lenguaje máquina (Ensamblador)
- **Alto nivel**: Cercanos al lenguaje humano (Java, Python, JavaScript)

---

## 🔤 Conceptos Básicos Fundamentales

### 1. Código Fuente
**Definición**: El texto que escriben los programadores en un lenguaje de programación.

```java
// Esto es código fuente
public class Saludo {
    public static void main(String[] args) {
        System.out.println("Hola Mundo");
    }
}
```

### 2. Compilación vs Interpretación

#### Lenguaje Compilado
El código fuente se traduce completamente a lenguaje máquina ANTES de ejecutarse.

```
Código Fuente (.java) → Compilador → Código Máquina (.class) → Ejecución
```

**Ventajas**:
- ✅ Ejecución más rápida
- ✅ Detecta errores antes de ejecutar
- ✅ Mayor optimización

**Ejemplos**: C, C++, Java (parcialmente)

#### Lenguaje Interpretado
El código se traduce línea por línea MIENTRAS se ejecuta.

```
Código Fuente (.py) → Intérprete → Ejecución directa
```

**Ventajas**:
- ✅ Desarrollo más rápido
- ✅ Más flexible
- ✅ Multiplataforma sin recompilar

**Ejemplos**: Python, JavaScript, Ruby

#### Java: Híbrido (Compilado e Interpretado)
```
Código Java (.java) → Compilador → Bytecode (.class) → JVM (Intérprete) → Ejecución
```

### 3. Sintaxis y Semántica

#### Sintaxis
Las **reglas gramaticales** del lenguaje (cómo se escribe).

```java
// Sintaxis CORRECTA
int edad = 25;

// Sintaxis INCORRECTA
int edad = 25  // Falta punto y coma
```

#### Semántica
El **significado** de lo que escribes (qué hace el código).

```java
// Sintaxis correcta, pero semántica incorrecta (no tiene sentido)
int edad = "veinticinco años";  // Error: String en variable int
```

### 4. Comentarios

**Propósito**: Explicar el código para humanos. La computadora los ignora.

```java
// Comentario de una línea

/*
 * Comentario de
 * múltiples líneas
 */

/**
 * JavaDoc: Documentación especial
 * @param nombre El nombre del usuario
 * @return Saludo personalizado
 */
```

---

## 📦 Variables y Tipos de Datos

### ¿Qué es una Variable?

Una **variable** es un contenedor con nombre que almacena un valor que puede cambiar.

```java
// Analogía: Una caja con etiqueta donde guardas cosas
int edad = 25;  // Caja llamada "edad" que contiene el número 25
```

### Declaración e Inicialización

```java
// Declaración (crear la caja)
int edad;

// Inicialización (poner algo en la caja)
edad = 25;

// Declaración e inicialización juntas
int edad = 25;
```

### Tipos de Datos Primitivos

#### 1. Números Enteros

```java
byte pequeño = 127;           // 8 bits: -128 a 127
short mediano = 32000;        // 16 bits: -32,768 a 32,767
int normal = 2000000;         // 32 bits: -2 mil millones a 2 mil millones
long grande = 9000000000L;    // 64 bits: números muy grandes
```

**Cuándo usar cada uno**:
- `byte`: Ahorrar memoria con números pequeños (edad, mes)
- `short`: Números medianos (año)
- `int`: **Uso general** (contador, cantidad)
- `long`: Números muy grandes (población mundial, dinero en centavos)

#### 2. Números Decimales

```java
float preciso = 3.14f;        // 32 bits: 6-7 dígitos decimales
double muypreciso = 3.14159;  // 64 bits: 15-16 dígitos decimales (RECOMENDADO)
```

**Cuándo usar**:
- `float`: Gráficos 3D, cálculos que no requieren mucha precisión
- `double`: **Uso general** (cálculos científicos, dinero)

#### 3. Caracteres

```java
char letra = 'A';             // Un solo caracter (16 bits Unicode)
char simbolo = '$';
char numero = '7';            // Es un caracter, NO un número
```

#### 4. Booleanos

```java
boolean esVerdadero = true;   // Verdadero
boolean esFalso = false;      // Falso
boolean esMayorDeEdad = edad >= 18;
```

### Tipos de Datos de Referencia

#### String (Cadenas de Texto)

```java
String nombre = "Juan";
String apellido = "Pérez";
String saludo = "Hola, " + nombre + " " + apellido;  // Concatenación

// Métodos útiles
int longitud = nombre.length();           // 4
String mayusculas = nombre.toUpperCase(); // "JUAN"
boolean contiene = nombre.contains("ua"); // true
char primeraLetra = nombre.charAt(0);     // 'J'
```

#### Arrays (Arreglos)

```java
// Declaración
int[] numeros = {1, 2, 3, 4, 5};
String[] nombres = new String[3];  // Array de tamaño 3

// Acceso
int primero = numeros[0];     // 1 (índices empiezan en 0)
numeros[2] = 10;              // Cambia el tercer elemento

// Longitud
int tamaño = numeros.length;  // 5
```

**Características**:
- ✅ Tamaño fijo
- ✅ Acceso rápido por índice
- ❌ No se puede cambiar el tamaño

### Constantes

```java
// Variables que NO cambian (mayúsculas por convención)
final double PI = 3.14159;
final int DIAS_SEMANA = 7;
final String NOMBRE_EMPRESA = "Baby Cash";

// Intentar cambiar da error
PI = 3.14;  // ❌ Error de compilación
```

### Scope (Alcance) de Variables

```java
public class Ejemplo {
    // Variable de clase (global a la clase)
    private int global = 10;
    
    public void metodo() {
        // Variable local (solo existe dentro del método)
        int local = 5;
        
        if (local > 0) {
            // Variable de bloque (solo existe dentro del if)
            int bloque = 3;
            System.out.println(local);   // ✅ Funciona
            System.out.println(global);  // ✅ Funciona
        }
        
        System.out.println(bloque);  // ❌ Error: bloque no existe aquí
    }
}
```

---

## ➕ Operadores

### Operadores Aritméticos

```java
int a = 10, b = 3;

int suma = a + b;           // 13
int resta = a - b;          // 7
int multiplicacion = a * b; // 30
int division = a / b;       // 3 (división entera)
int modulo = a % b;         // 1 (residuo de la división)

double divisionReal = 10.0 / 3.0;  // 3.333...
```

### Operadores de Asignación

```java
int x = 5;

x += 3;  // x = x + 3  →  8
x -= 2;  // x = x - 2  →  6
x *= 4;  // x = x * 4  →  24
x /= 6;  // x = x / 6  →  4
x %= 3;  // x = x % 3  →  1
```

### Operadores de Incremento/Decremento

```java
int contador = 5;

// Incremento
contador++;  // Post-incremento: contador = 6
++contador;  // Pre-incremento: contador = 7

// Decremento
contador--;  // Post-decremento: contador = 6
--contador;  // Pre-decremento: contador = 5

// Diferencia
int a = 5;
int b = a++;  // b = 5, a = 6 (primero asigna, luego incrementa)
int c = ++a;  // c = 7, a = 7 (primero incrementa, luego asigna)
```

### Operadores de Comparación

```java
int a = 5, b = 3;

boolean igual = (a == b);          // false
boolean diferente = (a != b);      // true
boolean mayor = (a > b);           // true
boolean menor = (a < b);           // false
boolean mayorIgual = (a >= b);     // true
boolean menorIgual = (a <= b);     // false
```

### Operadores Lógicos

```java
boolean a = true, b = false;

// AND (&&): Ambos deben ser verdaderos
boolean and = a && b;     // false
boolean and2 = true && true;  // true

// OR (||): Al menos uno debe ser verdadero
boolean or = a || b;      // true
boolean or2 = false || false;  // false

// NOT (!): Invierte el valor
boolean not = !a;         // false
boolean not2 = !b;        // true

// Ejemplo real
int edad = 20;
boolean tienePermiso = true;
boolean puedeConducir = (edad >= 18) && tienePermiso;  // true
```

### Operador Ternario

```java
// Sintaxis: condicion ? valorSiTrue : valorSiFalse
int edad = 17;
String mensaje = (edad >= 18) ? "Mayor de edad" : "Menor de edad";

// Equivalente a:
String mensaje;
if (edad >= 18) {
    mensaje = "Mayor de edad";
} else {
    mensaje = "Menor de edad";
}
```

---

## 🔄 Estructuras de Control

### Condicionales

#### If-Else

```java
int edad = 20;

if (edad >= 18) {
    System.out.println("Eres mayor de edad");
} else {
    System.out.println("Eres menor de edad");
}

// If-else-if
int nota = 85;

if (nota >= 90) {
    System.out.println("Excelente");
} else if (nota >= 80) {
    System.out.println("Muy bueno");
} else if (nota >= 70) {
    System.out.println("Bueno");
} else if (nota >= 60) {
    System.out.println("Suficiente");
} else {
    System.out.println("Insuficiente");
}
```

#### Switch

```java
int dia = 3;
String nombreDia;

switch (dia) {
    case 1:
        nombreDia = "Lunes";
        break;
    case 2:
        nombreDia = "Martes";
        break;
    case 3:
        nombreDia = "Miércoles";
        break;
    case 4:
        nombreDia = "Jueves";
        break;
    case 5:
        nombreDia = "Viernes";
        break;
    case 6:
        nombreDia = "Sábado";
        break;
    case 7:
        nombreDia = "Domingo";
        break;
    default:
        nombreDia = "Día inválido";
        break;
}

// Java 14+: Switch moderno
String nombreDia = switch (dia) {
    case 1 -> "Lunes";
    case 2 -> "Martes";
    case 3 -> "Miércoles";
    case 4 -> "Jueves";
    case 5 -> "Viernes";
    case 6, 7 -> "Fin de semana";
    default -> "Día inválido";
};
```

### Bucles (Loops)

#### For

```java
// Sintaxis: for (inicialización; condición; actualización)
for (int i = 0; i < 5; i++) {
    System.out.println("Iteración: " + i);
}
// Output: 0, 1, 2, 3, 4

// For-each (para recorrer colecciones)
String[] nombres = {"Ana", "Bob", "Carlos"};
for (String nombre : nombres) {
    System.out.println(nombre);
}
```

#### While

```java
int contador = 0;
while (contador < 5) {
    System.out.println("Contador: " + contador);
    contador++;
}

// Ejemplo: Validar entrada
Scanner scanner = new Scanner(System.in);
int numero = -1;
while (numero < 0) {
    System.out.println("Ingresa un número positivo:");
    numero = scanner.nextInt();
}
```

#### Do-While

```java
// Se ejecuta AL MENOS UNA VEZ, luego verifica la condición
int numero;
do {
    System.out.println("Ingresa un número entre 1 y 10:");
    numero = scanner.nextInt();
} while (numero < 1 || numero > 10);
```

### Control de Flujo

#### Break

```java
// Sale del bucle inmediatamente
for (int i = 0; i < 10; i++) {
    if (i == 5) {
        break;  // Sale cuando i es 5
    }
    System.out.println(i);  // Output: 0, 1, 2, 3, 4
}
```

#### Continue

```java
// Salta a la siguiente iteración
for (int i = 0; i < 5; i++) {
    if (i == 2) {
        continue;  // Salta cuando i es 2
    }
    System.out.println(i);  // Output: 0, 1, 3, 4
}
```

#### Return

```java
public int buscarNumero(int[] array, int objetivo) {
    for (int i = 0; i < array.length; i++) {
        if (array[i] == objetivo) {
            return i;  // Sale del método inmediatamente
        }
    }
    return -1;  // No encontrado
}
```

---

## 🔧 Funciones y Métodos

### ¿Qué es una Función?

Un **bloque de código reutilizable** que realiza una tarea específica.

**Beneficios**:
- ✅ Reutilización de código
- ✅ Código más organizado
- ✅ Más fácil de probar
- ✅ Más fácil de mantener

### Anatomía de un Método

```java
// Modificador  TipoRetorno  Nombre      Parámetros
   public       int          suma        (int a, int b) {
       // Cuerpo del método
       int resultado = a + b;
       return resultado;  // Devuelve el resultado
   }
```

### Tipos de Métodos

#### Sin Parámetros, Sin Retorno

```java
public void saludar() {
    System.out.println("¡Hola!");
}

// Uso
saludar();  // Output: ¡Hola!
```

#### Con Parámetros, Sin Retorno

```java
public void saludarPersona(String nombre) {
    System.out.println("¡Hola, " + nombre + "!");
}

// Uso
saludarPersona("Juan");  // Output: ¡Hola, Juan!
```

#### Sin Parámetros, Con Retorno

```java
public int obtenerEdad() {
    return 25;
}

// Uso
int edad = obtenerEdad();  // edad = 25
```

#### Con Parámetros, Con Retorno

```java
public int suma(int a, int b) {
    return a + b;
}

public double calcularPromedio(double[] numeros) {
    double suma = 0;
    for (double num : numeros) {
        suma += num;
    }
    return suma / numeros.length;
}

// Uso
int resultado = suma(5, 3);  // resultado = 8
double promedio = calcularPromedio(new double[]{8.5, 9.0, 7.5});  // 8.33
```

### Sobrecarga de Métodos (Overloading)

Múltiples métodos con el **mismo nombre** pero **diferentes parámetros**.

```java
public int suma(int a, int b) {
    return a + b;
}

public double suma(double a, double b) {
    return a + b;
}

public int suma(int a, int b, int c) {
    return a + b + c;
}

// Uso
int resultado1 = suma(5, 3);           // Usa el primero
double resultado2 = suma(5.5, 3.2);    // Usa el segundo
int resultado3 = suma(5, 3, 2);        // Usa el tercero
```

### Recursión

Un método que **se llama a sí mismo**.

```java
// Factorial: 5! = 5 × 4 × 3 × 2 × 1 = 120
public int factorial(int n) {
    if (n == 0 || n == 1) {
        return 1;  // Caso base
    }
    return n * factorial(n - 1);  // Llamada recursiva
}

// Uso
int resultado = factorial(5);  // 120

// Flujo:
// factorial(5) = 5 * factorial(4)
// factorial(4) = 4 * factorial(3)
// factorial(3) = 3 * factorial(2)
// factorial(2) = 2 * factorial(1)
// factorial(1) = 1
// Resultado: 5 * 4 * 3 * 2 * 1 = 120
```

---

## 📊 Estructuras de Datos

### Arrays (Arreglos)

```java
// Declaración
int[] numeros = new int[5];          // Array de 5 enteros
String[] nombres = {"Ana", "Bob"};   // Array inicializado

// Acceso
int primero = numeros[0];
numeros[2] = 10;

// Recorrer
for (int i = 0; i < numeros.length; i++) {
    System.out.println(numeros[i]);
}

// Arrays multidimensionales
int[][] matriz = {
    {1, 2, 3},
    {4, 5, 6},
    {7, 8, 9}
};
int elemento = matriz[1][2];  // 6
```

### Listas (ArrayList)

```java
import java.util.ArrayList;

// Crear lista
ArrayList<String> nombres = new ArrayList<>();

// Agregar elementos
nombres.add("Ana");
nombres.add("Bob");
nombres.add("Carlos");

// Acceder
String primero = nombres.get(0);  // "Ana"

// Modificar
nombres.set(1, "Roberto");  // Cambia "Bob" por "Roberto"

// Eliminar
nombres.remove(0);          // Elimina "Ana"
nombres.remove("Carlos");   // Elimina por valor

// Tamaño
int tamaño = nombres.size();

// Verificar si existe
boolean existe = nombres.contains("Roberto");  // true

// Recorrer
for (String nombre : nombres) {
    System.out.println(nombre);
}
```

**Arrays vs ArrayList**:

| Arrays | ArrayList |
|--------|-----------|
| Tamaño fijo | Tamaño dinámico |
| Más rápido | Más lento |
| Primitivos (int, double) | Solo objetos (Integer, Double) |
| `array[i]` | `list.get(i)` |

### Maps (Diccionarios)

```java
import java.util.HashMap;

// Crear map (clave → valor)
HashMap<String, Integer> edades = new HashMap<>();

// Agregar
edades.put("Ana", 25);
edades.put("Bob", 30);
edades.put("Carlos", 28);

// Obtener
int edadAna = edades.get("Ana");  // 25

// Verificar si existe clave
boolean existe = edades.containsKey("Ana");  // true

// Verificar si existe valor
boolean tieneEdad30 = edades.containsValue(30);  // true

// Eliminar
edades.remove("Bob");

// Recorrer
for (String nombre : edades.keySet()) {
    int edad = edades.get(nombre);
    System.out.println(nombre + ": " + edad);
}

// Recorrer con entradas
for (Map.Entry<String, Integer> entrada : edades.entrySet()) {
    System.out.println(entrada.getKey() + ": " + entrada.getValue());
}
```

### Sets (Conjuntos)

```java
import java.util.HashSet;

// Crear set (no permite duplicados)
HashSet<String> frutas = new HashSet<>();

// Agregar
frutas.add("Manzana");
frutas.add("Banana");
frutas.add("Manzana");  // No se agrega (ya existe)

// Tamaño
int cantidad = frutas.size();  // 2 (no cuenta duplicados)

// Verificar
boolean tiene = frutas.contains("Banana");  // true

// Eliminar
frutas.remove("Banana");

// Recorrer
for (String fruta : frutas) {
    System.out.println(fruta);
}
```

---

## 🏗️ Programación Orientada a Objetos

### ¿Qué es un Objeto?

Un objeto es una **entidad** que tiene:
- **Atributos** (características)
- **Métodos** (comportamientos)

**Analogía**: Un carro
- **Atributos**: color, marca, modelo, velocidad
- **Métodos**: acelerar(), frenar(), girar()

### Clase

Una **plantilla** para crear objetos.

```java
public class Persona {
    // Atributos (estado)
    private String nombre;
    private int edad;
    
    // Constructor
    public Persona(String nombre, int edad) {
        this.nombre = nombre;
        this.edad = edad;
    }
    
    // Métodos (comportamiento)
    public void saludar() {
        System.out.println("Hola, soy " + nombre);
    }
    
    public boolean esMayorDeEdad() {
        return edad >= 18;
    }
    
    // Getters y Setters
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public int getEdad() {
        return edad;
    }
    
    public void setEdad(int edad) {
        if (edad > 0) {
            this.edad = edad;
        }
    }
}

// Uso
Persona persona1 = new Persona("Juan", 25);
persona1.saludar();  // Output: Hola, soy Juan
boolean esMayor = persona1.esMayorDeEdad();  // true
```

### Los 4 Pilares de la POO

#### 1. Encapsulación

**Ocultar** los detalles internos y exponer solo lo necesario.

```java
public class CuentaBancaria {
    private double saldo;  // Private: no se puede acceder directamente
    
    public void depositar(double monto) {
        if (monto > 0) {
            saldo += monto;
        }
    }
    
    public void retirar(double monto) {
        if (monto > 0 && monto <= saldo) {
            saldo -= monto;
        }
    }
    
    public double getSaldo() {
        return saldo;  // Solo lectura, no modificación directa
    }
}

// Uso
CuentaBancaria cuenta = new CuentaBancaria();
cuenta.depositar(1000);
cuenta.retirar(200);
// cuenta.saldo = -500;  // ❌ Error: saldo es privado
```

#### 2. Herencia

Una clase **hereda** atributos y métodos de otra clase.

```java
// Clase padre (superclase)
public class Animal {
    protected String nombre;
    
    public void comer() {
        System.out.println(nombre + " está comiendo");
    }
}

// Clase hija (subclase)
public class Perro extends Animal {
    public void ladrar() {
        System.out.println(nombre + " está ladrando");
    }
}

// Uso
Perro perro = new Perro();
perro.nombre = "Firulais";
perro.comer();   // Heredado de Animal
perro.ladrar();  // Propio de Perro
```

#### 3. Polimorfismo

**Muchas formas**: Un mismo método se comporta diferente según el objeto.

```java
public class Animal {
    public void hacerSonido() {
        System.out.println("El animal hace un sonido");
    }
}

public class Perro extends Animal {
    @Override
    public void hacerSonido() {
        System.out.println("Guau guau");
    }
}

public class Gato extends Animal {
    @Override
    public void hacerSonido() {
        System.out.println("Miau miau");
    }
}

// Uso (polimorfismo)
Animal animal1 = new Perro();
Animal animal2 = new Gato();

animal1.hacerSonido();  // Output: Guau guau
animal2.hacerSonido();  // Output: Miau miau
```

#### 4. Abstracción

**Ocultar complejidad** y mostrar solo lo esencial.

```java
public abstract class FiguraGeometrica {
    protected String color;
    
    // Método abstracto (sin implementación)
    public abstract double calcularArea();
    
    // Método concreto
    public void pintar() {
        System.out.println("Pintando de color " + color);
    }
}

public class Circulo extends FiguraGeometrica {
    private double radio;
    
    @Override
    public double calcularArea() {
        return Math.PI * radio * radio;
    }
}

public class Rectangulo extends FiguraGeometrica {
    private double base;
    private double altura;
    
    @Override
    public double calcularArea() {
        return base * altura;
    }
}
```

---

## ⚠️ Manejo de Errores

### Excepciones

**Situaciones inesperadas** que ocurren durante la ejecución del programa.

```java
// Sin manejo de excepciones
int[] numeros = {1, 2, 3};
int valor = numeros[5];  // ❌ Error: índice fuera de rango

// Con manejo de excepciones
try {
    int valor = numeros[5];
    System.out.println(valor);
} catch (ArrayIndexOutOfBoundsException e) {
    System.out.println("Error: Índice inválido");
} finally {
    System.out.println("Esto siempre se ejecuta");
}
```

### Try-Catch-Finally

```java
public void leerArchivo(String ruta) {
    try {
        // Código que puede fallar
        FileReader archivo = new FileReader(ruta);
        // Leer archivo...
    } catch (FileNotFoundException e) {
        // Manejar error específico
        System.out.println("Archivo no encontrado: " + e.getMessage());
    } catch (IOException e) {
        // Manejar otro tipo de error
        System.out.println("Error de lectura: " + e.getMessage());
    } finally {
        // Siempre se ejecuta (cerrar recursos, etc.)
        System.out.println("Proceso terminado");
    }
}
```

### Lanzar Excepciones

```java
public void validarEdad(int edad) throws Exception {
    if (edad < 0) {
        throw new Exception("La edad no puede ser negativa");
    }
    if (edad < 18) {
        throw new Exception("Debe ser mayor de edad");
    }
    System.out.println("Edad válida");
}

// Uso
try {
    validarEdad(15);
} catch (Exception e) {
    System.out.println("Error: " + e.getMessage());
}
```

---

## 🧮 Algoritmos Básicos

### Búsqueda Lineal

```java
public int busquedaLineal(int[] array, int objetivo) {
    for (int i = 0; i < array.length; i++) {
        if (array[i] == objetivo) {
            return i;  // Encontrado
        }
    }
    return -1;  // No encontrado
}
```

### Búsqueda Binaria (Array ordenado)

```java
public int busquedaBinaria(int[] array, int objetivo) {
    int izquierda = 0;
    int derecha = array.length - 1;
    
    while (izquierda <= derecha) {
        int medio = (izquierda + derecha) / 2;
        
        if (array[medio] == objetivo) {
            return medio;
        } else if (array[medio] < objetivo) {
            izquierda = medio + 1;
        } else {
            derecha = medio - 1;
        }
    }
    
    return -1;
}
```

### Ordenamiento Burbuja

```java
public void ordenamientoBurbuja(int[] array) {
    int n = array.length;
    for (int i = 0; i < n - 1; i++) {
        for (int j = 0; j < n - i - 1; j++) {
            if (array[j] > array[j + 1]) {
                // Intercambiar
                int temp = array[j];
                array[j] = array[j + 1];
                array[j + 1] = temp;
            }
        }
    }
}
```

---

## ✅ Resumen de Conceptos Clave

| Concepto | Definición Breve |
|----------|------------------|
| **Variable** | Contenedor con nombre que almacena un valor |
| **Tipo de Dato** | Clasificación del tipo de valor (int, String, etc.) |
| **Operador** | Símbolo que realiza operaciones (+, -, ==, etc.) |
| **Condicional** | Ejecuta código según una condición (if, switch) |
| **Bucle** | Repite código múltiples veces (for, while) |
| **Función** | Bloque de código reutilizable |
| **Array** | Colección de elementos del mismo tipo |
| **Clase** | Plantilla para crear objetos |
| **Objeto** | Instancia de una clase |
| **Encapsulación** | Ocultar detalles internos |
| **Herencia** | Clase hija hereda de clase padre |
| **Polimorfismo** | Mismo método, diferentes comportamientos |
| **Excepción** | Error en tiempo de ejecución |

---

**Documento creado**: 4 de Noviembre de 2025  
**Propósito**: Fundamentos de programación desde cero  
**Proyecto**: Baby Cash - SENA
