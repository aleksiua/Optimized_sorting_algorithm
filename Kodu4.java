import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.HashMap;
/***********************************
 * Algoritmid ja andmestruktuurid. LTAT.03.005
 * 2024/2025 kevadsemester
 *
 * Kodutöö nr 4
 * Teema: Paikstabelid
 *
 * Autor: Annabel Aleksius
 *
 **********************************/
public class Kodu4 {
//kimbumeetod, ei eeilda et koik erinevad.
    /**
     * Genereerib isikukoodi lähtudes reeglitest püstitatud <a href=https://et.wikipedia.org/wiki/Isikukood>siin.</a>
     * <br>
     * Numbrite tähendused:
     * <ul style="list-style-type:none">
     *      <li> 1 - sugu ja sünniaasta esimesed kaks numbrit, (1...😎 </li>
     *      <li> 2-3 - sünniaasta 3. ja 4. numbrid, (00...99) </li>
     *      <li> 4-5 - sünnikuu, (01...12) </li>
     *      <li> 6-7 - sünnikuupäev (01...31) </li>
     *      <li> 8-10 - järjekorranumber samal päeval sündinute eristamiseks (000...999) </li>
     *      <li> 11 - kontrollnumber (0...9) </li>
     * </ul>
     *
     * @return Eesti isikukoodi reeglitele vastav isikukood
     */
    static long genereeriIsikukood() {
        ThreadLocalRandom juhus = ThreadLocalRandom.current();
        LocalDate sünnikuupäev = LocalDate.ofEpochDay(juhus.nextLong(-62091, 84006)); // Suvaline kuupäeva a 1800-2199
        long kood = ((sünnikuupäev.getYear() - 1700) / 100 * 2 - juhus.nextInt(2)) * (long) Math.pow(10, 9) +
                sünnikuupäev.getYear() % 100 * (long) Math.pow(10, 7) +
                sünnikuupäev.getMonthValue() * (long) Math.pow(10, 5) +
                sünnikuupäev.getDayOfMonth() * (long) Math.pow(10, 3) +
                juhus.nextInt(1000);
        int korrutisteSumma = 0;
        int[] iAstmeKaalud = {1, 2, 3, 4, 5, 6, 7, 8, 9, 1};
        for (int i = 0; i < 10; i++) {
            korrutisteSumma += (int) (kood / (long) Math.pow(10, i) % 10 * iAstmeKaalud[9 - i]);
        }
        int kontroll = korrutisteSumma % 11;
        if (kontroll == 10) {
            int[] iiAstmeKaalud = {3, 4, 5, 6, 7, 8, 9, 1, 2, 3};
            korrutisteSumma = 0;
            for (int i = 0; i < 10; i++) {
                korrutisteSumma += (int) (kood / (long) Math.pow(10, i) % 10 * iiAstmeKaalud[9 - i]);
            }
            kontroll = korrutisteSumma % 11;
            kontroll = kontroll < 10 ? kontroll : 0;
        }
        return kood * 10 + kontroll;
    }

    /**
     * Sorteerib isikukoodid sünniaja järgi:
     * <ul style="list-style-type:none">
     *     <li> a) järjestuse aluseks on sünniaeg, vanemad inimesed on eespool; </li>
     *     <li> b) kui sünniajad on võrdsed, määrab järjestuse isikukoodi järjekorranumber (kohad 8-10); </li>
     *     <li> c) kui ka järjekorranumber on võrdne, siis määrab järjestuse esimene number. </li>
     * </ul>
     *
     * jagan  isikukoodid "kimpudeks" sõltuvalt isikukoodi esimese numbri põhjal.
     * sorteerin iga kimbu järgi sünniaja, järjekorranumbri ja esimese numbri alusel
     * võrdlen sünniaega (aasta, kuu, päev).
     * kui sünniajad on võrdsed, vaatan järjekorranumbrit
     * kui need on ka võrdsed, esimest numbrit.
     * lõpus panen kõik kimbud tagasi ühte järjestatud massiivi.
     *
     * @param isikukoodid sorteeritav isikukoodide massiiv
     */

    public static void sort(long[] isikukoodid) {
        HashMap<Integer, List<Long>> järjend= new HashMap<>();
        for (long id : isikukoodid) {
            int võti = 0;
            int essa = Integer.parseInt(Long.toString(id).substring(0, 1));

            if (essa == 1 || essa == 2) {
                võti = 1;
            } else if (essa == 3 || essa == 4) {
                võti = 2;
            } else if (essa == 5 || essa == 6) {
                võti = 3;
            } else {
                võti = 4;
            }
            järjend.computeIfAbsent(võti, k -> new ArrayList<>()).add(id);
        }
        for (int key : järjend.keySet()) {
            List<Long> bucket = järjend.get(key);
            bucket.sort((x1, x2) -> {
                int aasta1 = Integer.parseInt(Long.toString(x1).substring(1, 3));
                int aasta2 = Integer.parseInt(Long.toString(x2).substring(1, 3));

                int kuu1 = Integer.parseInt(Long.toString(x1).substring(3, 5));
                int kuu2 = Integer.parseInt(Long.toString(x2).substring(3, 5));


                int päev1 = Integer.parseInt(Long.toString(x1).substring(5, 7));
                int päev2 = Integer.parseInt(Long.toString(x2).substring(5, 7));

                int snr1 = Integer.parseInt(Long.toString(x1).substring(7, 10));
                int snr2 = Integer.parseInt(Long.toString(x2).substring(7, 10));

                int sajand1 = Integer.parseInt(Long.toString(x1).substring(0, 1));
                int sajand2 = Integer.parseInt(Long.toString(x2).substring(0, 1));

                if (aasta1 != aasta2) return Integer.compare(aasta1, aasta2);
                if (kuu1 != kuu2) return Integer.compare(kuu1, kuu2);
                if (päev1 != päev2) return Integer.compare(päev1, päev2);
                if (snr1 != snr2) return Integer.compare(snr1, snr2);
                return Integer.compare(sajand1, sajand2);
            });
        }
        int indeks = 0;
        for (int i : järjend.keySet()) {
            List<Long> kimp = järjend.get(i);
            for (Long j : kimp) {
                isikukoodid[indeks++] = j;
            }
        }
    }

    public static void main(String[] args) {
        long[] isikukoodid = new long[10];
        for (int i = 0; i < 10; i++) {
            isikukoodid[i] = genereeriIsikukood();
        }
        System.out.println("enne: ");
        System.out.println(Arrays.toString(isikukoodid));
        sort(isikukoodid);
        System.out.println("sorteeritult: ");
        System.out.println(Arrays.toString(isikukoodid));
    }

}