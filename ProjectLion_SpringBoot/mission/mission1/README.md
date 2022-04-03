# Interface 사용해보기

Java의 Interface가 뭔지 직접 다뤄봅시다.

이번 미션은 인터페이스에 대한 지식이 정확하다면 챌린지 미션이 베이직 미션보다 쉽습니다.

## Basic Mission

1. 사람을 나타내는 `Person` 인터페이스를 정의하고,
2. 사람을 구현하는 추상 클래스 `AbstractPerson` 을 구현한 다음,
3. `AbstractPerson` 을 확장하는 `Student` 와 `Lecturer` 클래스를 각각 만들어보세요.

### 세부 조건

1. `Person` 인터페이스는 사람을 나타내는 인터페이스로서, `void speak()` 함수를 가지고 있습니다. `Person` 인터페이스의 구현체는 `speak` 함수를 통해 자신의 정보를 출력합니다.
2. `AbstractPerson` 추상 클래스는 사람이라면 공통적으로 가지는 정보, 기능 등을 구현하기 위한 클래스 입니다. 이름, 나이 정보 등을 가지고 있으며, `speak` 함수도 사용할 수 있습니다.
3. `Student` 와 `Lecturer` 는 `AbstractPerson` 을 extend 하며, `speak` 함수를 사용할 때 자신의 이름과 학생인지, 강사인지를 이야기해줍니다.

## Challenge Mission

1. Java 클래스 중 `ArrayList`, `LinkedList`, `Vector`, `HashSet` 를 찾아봅시다. 클래스들의 공통점을 살펴보세요.
2. 위에서 언급한 클래스들은 전부 여러 객체를 들고있을 수 있습니다. 이 클래스가 가지고 있는 `item` 을 형식을 맞춰 출력하는 함수를 작성해 봅시다.

### 세부 조건

1. 출력은 아래와 같은 형식으로 작성합니다.

    ```
    idx item
    0   Item 1
    1   Item 2
    
    ...
    
    n   Item n
    ```

2. `Item n` 부분은 클래스의 Item (또는 Element)를 `String`으로 전환했을때의 값으로 나오면 됩니다.
3. Item이 없으면 `No Elements` 라고 출력합니다.

