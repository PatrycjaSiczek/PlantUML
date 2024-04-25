import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Main {
    public static void main(String args[]) {
      //  String data = "@startuml\n" + "object Jan\n" + "object Anna\n" + "\n" + "Jan --> Anna\n" + "@enduml";
        PlantUMLRunner.setJarPath("C:\\Users\\patry\\OneDrive\\Pulpit\\Java programowanie obiektowe\\Projekt2\\plantuml-1.2024.4.jar");
      //  PlantUMLRunner.generate(data, "uml", "test");
       try {
//            p = Person.fromCsvLine("Anna Dąbrowska,07.02.1930,22.12.1991,Ewa Kowalska,Marek Kowalski");
//            System.out.println(p.toString());
           List<Person> people = Person.fromCsv("family.csv");//pobieramy liste osob
           for (Person person : people)
               System.out.println(person);
           Person person = people.get(2);
           String uml = person.toUML();
           PlantUMLRunner.generate(uml, "uml", person.name);
//            }
   } catch (IOException e) {
          throw new RuntimeException(e);}}}

//       System.out.println(e.getMessage());
//       System.err.println(e.getMessage());
//       System.out.println("Koniec");

//            Optional<String> changedPeople = people
//List<Person> people = Person.fromCsv("family.csv");//pobieramy liste osob, chcac odfiltrowac liste osob ktore sa np jan kowalski
//                    .stream()//analizujemy element po elemencie
//                    .sorted((person1, person2) -> person1.name.compareTo(person2.name))
//                    .map(person -> person.name)//kazda osobe przetwazamy na imie -zamiana z person na name, od tej pory to name
//                    .filter(name -> !name.equals("Anna Dąbrowska"))//wykorzystujemt lambde -->
//                    .max((name1, name2) -> name1.compareTo(name2));
//                    .collect(Collectors.toList());

//            if(!changedPeople.isEmpty()) {
//                System.out.println(changedPeople.get());
//            }

//            for(String person: changedPeople)
//                System.out.println(person);
//
//            String uml = Person.toUML(people);
//            PlantUMLRunner.generate(uml, "uml", "family");

//            Person person = people.get(2);
//            String uml = person.toUML();
//            PlantUMLRunner.generate(uml, "uml", person.name);
