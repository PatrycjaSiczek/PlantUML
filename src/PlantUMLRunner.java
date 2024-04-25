import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

//Napisz klasę PlantUMLRunner, posiadającą publiczne statyczne metody:
// -ustawienie ścieżki do uruchamialnego pliku jar.
//- wygenerowanie schematu po przekazaniu: napisu z danymi, ścieżki do katalogu wynikowego i nazwy pliku wynikowego.


public class PlantUMLRunner {
    public static String jarPath;// sciezka do pliku jar-plant uml

    public static void setJarPath(String jarPath) {
        PlantUMLRunner.jarPath = jarPath;//metoda ustawiajaca sciezke do pliku jar narzedzia plantuml
    }

    public static void generate(String data, String path, String fileName) {//wygenerowany schemat na podstawie danych wejsciowych
        try {
            File catalog = new File(path);//path sciezka do pliku
            catalog.mkdirs();//metoda mkdirs tworzy katalog jesli nie istnieje
            //zapisanie daty do pliku o takiej sciezce
            String filePath = path + '/' + fileName + ".txt";//realna siezka do pliku do ktorego zapisujem data
            FileWriter writer = new FileWriter(filePath);//zapis danych do pliku, jest nieobsluzony wyjatek wiec trzeba dodac albo try catch albo do sygnayuty metode
            writer.write(data);//przekazanie do pliku
            writer.close();//zawsze zamykamy

            //wywolanie plantUML z poziomu javy nie terminala
            //podporoces ktory pozwala na wygenerowanie nogo obrazka najpierw budowanie pozniej wykonywanie
            //java -jar plantuml-1.2024.4jar text.txt

            ProcessBuilder builder = new ProcessBuilder(
                    "java",
                    "-jar",
                    PlantUMLRunner.jarPath,
                    filePath);//zbudowanie  UNIWERSALNA METODA NA WYLOWYWANIE PROGRAMOW Z TERMINALA
            Process process = builder.start();//wowolujemy proces i czekamy na koniec
            process.waitFor();
        } catch (IOException | InterruptedException e) { // wyjatek do wpisywania do plku i procesu waitfor()
            throw new RuntimeException(e);
        }
    }
}
