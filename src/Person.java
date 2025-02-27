import javax.xml.transform.Result;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
public class Person implements Serializable {
    public final String name;
    public final LocalDate birth, death;
    private List<Person> parents = new ArrayList<>(); //jak sa jacys rodzice to dodajemy ich do projektu
    public Person(String name, LocalDate birth, LocalDate death) {
        this.name = name;
        this.birth = birth;
        this.death = death;
    }
    public static Person fromCsvLine(String line) {
        String[] fields = line.split(",");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
//data urodzin
        String birthString = fields[1];
        String deathString = fields[2];
        LocalDate birth = null, death = null;
        if(!birthString.isEmpty())
            birth = LocalDate.parse(birthString, formatter);
        if(!deathString.isEmpty())
            death = LocalDate.parse(deathString, formatter);

        return new Person(fields[0], birth, death);
    }

//tworzy liste osob na podstawie pliku
    public static List<Person> fromCsv(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        List<Person> result = new ArrayList<>();
        List<PersonWithParentsNames> resultWithParents =  new ArrayList<>();
        //oczytuje linie i przetwaza je
        String line;
        br.readLine();
        try {
            while ((line = br.readLine()) != null) {
                //tworzy obiekt personpaentsname z lini svg
                PersonWithParentsNames personWithNames = PersonWithParentsNames.fromCsvLine(line);
              //dlugosc zycia
                personWithNames.person.validateLifespan();
               //dodaje obiekt do listy
                personWithNames.person.validateAmbiguity(result);
                resultWithParents.add(personWithNames);
                result.add(personWithNames.person);
            }
            //wiek rodzicow
            PersonWithParentsNames.linkRelatives(resultWithParents);
            try {
                for(Person person: result)
                    person.validateParentingAge();
            }
            catch(ParentingAgeException exception) {
                Scanner scanner = new Scanner(System.in);
                System.out.println(exception.getMessage());
                System.out.println("Please confirm [Y/N]:");
                String response = scanner.nextLine();
                if(!response.equals("Y") && !response.equals("y"))
                    result.remove(exception.person);
            }
        } catch (NegativeLifespanException | AmbiguousPersonException | UndefinedParentException exception) {
            System.err.println(exception.getMessage());
        }
        return result;
    }
    //dugosc zycia
    private void validateLifespan() throws NegativeLifespanException {
        if(this.death != null && this.birth.isAfter(this.death))
            throw new NegativeLifespanException(this);
    }
//unikalnosc osoby
    private void validateAmbiguity(List<Person> peopleSoFar) throws AmbiguousPersonException {
        for(Person person: peopleSoFar)
            if(person.name.equals(this.name))
                throw new AmbiguousPersonException(name);
    }
//wiek rodzicow osoby
    private void validateParentingAge() throws ParentingAgeException {
        for(Person parent: parents)
            if (birth.isBefore(parent.birth.minusYears(15)) || ( parent.death != null && birth.isAfter(parent.death)))
                throw new ParentingAgeException(this, parent);
    }

    public void addParent(Person person) {
        parents.add(person);
    }
//dodaje rodzica do osoby
    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", birth=" + birth +
                ", death=" + death +
                ", parents=" + parents +
                '}';
    }
//zapisuje do pliku binarnego
    public static void toBinaryFile(List<Person> people, String filename) throws IOException {
        try (
                FileOutputStream fos = new FileOutputStream(filename);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(people);
        }
    }
//wczytuje liste osob z pliku binarnego
    public static List<Person> fromBinaryFile(String filename) throws IOException, ClassNotFoundException {
        try (
                FileInputStream fis = new FileInputStream(filename);
                ObjectInputStream ois = new ObjectInputStream(fis);
        ) {
            return (List<Person>) ois.readObject();
        }
    }
//    W klasie Person napisz bezargumentową metodę, która zwróci napis sformatowany według składni PlantUML.
//    Napis, korzystając z diagramu obiektów, powinien przedstawiać obiekt osoby na rzecz której została wywołana metoda oraz jej rodziców (o ile są zdefiniowani).
//    Obiekty powinny zawierać nazwę osoby. Od dziecka do rodziców należy poprowadzić strzałki.
    public String toUML() {
        StringBuilder objects = new StringBuilder(); //wydajnie tworzy string obiekty Anna Jan
        StringBuilder relations = new StringBuilder(); //relacje Jan --> Anna

        //LAMBDA!! usuwa spacje z nazw
        Function<String, String>
        replaceSpaces = str -> str.replaceAll(" ", "");//to po strzalce zastepuje

        objects.append("object " + replaceSpaces.apply(name) + "\n");

        for(Person parent : parents) {//w kazdym wywloaniu parent nowy bedzie podsawiany
            objects.append("object " +  replaceSpaces.apply(parent.name) + "\n");//append dodaje cos do buildera, po + sie dodaje wiec append
          //dodaje relacje miedzy obiektem a rodzicami
            relations.append(replaceSpaces.apply(name) + " <-- " +  replaceSpaces.apply(parent.name) + "\n");
        }

        return String.format(
                "@startuml\n%s\n%s\n@enduml",
                objects,
                relations
        );
    }
//zwraca liste osob w plantuml
    public static String toUML(List<Person> people) {
        Set<String> objects = new HashSet<>();//bez powtorek
        Set<String> relations = new HashSet<>();

        Function<String, String> replaceSpaces = str -> str.replaceAll(" ", "");//lambda usuwa spacje z nazw

        for(Person person : people) {
            objects.add("object " + replaceSpaces.apply(person.name));

            for(Person parent : person.parents) {
                objects.add("object " +  replaceSpaces.apply(parent.name));
                relations.add(replaceSpaces.apply(person.name) + " <-- " +  replaceSpaces.apply(parent.name) + "\n");
            }
        }

        return String.format(
                "@startuml\n%s\n%s\n@enduml",
                String.join("\n", objects),
                String.join("", relations)
        );
    }
}
//Napisz klasę Person, w której znajdować będą się dane odpowiadające wierszowi pliku.
//Na tym etapie pomiń wczytywanie rodziców.
//Napisz metodę wytwórczą fromCsvLine() klasy Person przyjmującą jako argument linię opisanego pliku.

