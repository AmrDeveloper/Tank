# Tank

## It's Dynamic type Object oriented and scripting language written in Java
## Porting version of Jlox

## Features
- import native modules
- Classes
- Array data structure with dynamic types
- Single Inheritance like Java
- this keyword
- Methods
- Functions
- Function Extenson
- Local functions
- Native Functions
- Ternary (X ? Y : Z) and Elvis (X ? Y) Operators
- If condition
- While loop
- Do While Loop
- Repeat block like Kotlin
- Scope and Block
- super, this keywords for oop
- Break and Continue keyword for loops
- Logical operators AND, OR, XOR
- Bitwise operators <<, >>, >>>
- Function can take other Function as Parameter
- Runtime error
- Semantic analysis
- Arity Similar to Python that throw error if developer pass less or more than function arguments

#### Simple Examples
    module tank.scanner;
    module tank.string;
    
    var name = scanString();
    print("Hello " + name + "\n");
    
    var nameLength = strLength(name);
    print(nameLength);
    
    -> Input
    Amr
    -> Output
    Hello Amr
    3
####
    module tank.system;
    
    print("Current os name is : " + osName() + "\n");
    
    print("JDK Path : " + osEnvVar("JAVA_HOME") + "\n");
    
    print("OS Arch : " + osArch() + "\n");
    
    -> Output
    Current os name is : Windows 10
    JDK Path : C:\Program Files\Java\jdk1.8.0_171
    OS Arch : amd64
    
#### For all Examples see examples directory
