# Cerebro
Το Cerebro είναι μία εφαρμογή που αναπτύχθηκε στα πλαίσια του μαθήματος Ανάπτυξη Λογισμικού για Δίκτυα και Τηλεπικοινωνίες. Σκοπός είναι η απομακρυσμένη διαχείριση συσκευών μέσω σημάτων που παράγονται από τον ανθρώπινο εγκέφαλο χρησιμοποιώντας δεδομένα που συλλέχθηκαν με τη χρήση του φορητού εγκεφαλογραφήματος Emotiv EPOC++.

## Android app :iphone:
Η Android εφαρμογή Cerebro αποτελείται από μία Main Activity που περιέχει κουμπιά on/off για την ενεργοποίηση του φακού και της ηχητικής ειδοποίησης, καθώς και ένα μενού που περιέχει επιλογές για: παραμετροποίηση της IP και του port μέσω των οποίων μπορεί να επικοινωνήσει με τον broker, παραμετροποίηση της συχνότητας με την οποία λαμβάνει εντολές και τέλος επιλογή εξόδου.

Έπειτα από την εισαγωγή των IP και port δημιουργεί ένα αντικείμενο της κλάσης MqttClient προκειμένου να πραγματοποιήσει αμφίδρομη επικοινωνία με το java app. Πιο συγκεκριμένα δέχεται εντολές τύπου on/off και στέλνει την συχνότητα με την οποία επιθυμεί να λαμβάνει τις εντολές αυτές.

Λόγω της αναγκαιότητας αμφίδρομης επικοινωνίας είναι απαραίτητος και ο συνεχής έλεγχος ύπαρξης σύνδεσης στο διαδίκτυο. Για τον σκοπό αυτό δημιουργήθηκαν τα αρχεία *ServiceManager.java* και *WifiReceiver.java*. Επίσης, προστέθηκαν τα απαραίτητα permissions:
```java
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
```
Οπότε στο αρχείο *MainActivity.java* και πιο συγκεκριμένα στη συνάρτηση *onStart()* με την βοήθεια ενός handler και ενός registerReceiver, ζητάμε να μάθουμε την ύπαρξη σύνδεσης ανά 8s αφού έχουμε κατασκευάσει τον *wifiManager* στην *onCreate()*.
```java
final int delay = 8; // seconds
wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
handler.postDelayed(new Runnable(){
    public void run(){
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        handler.postDelayed(this, delay * 1000);
    }
}, delay);
```

Για να επιβεβαιώσουμε ότι όντως ελέγχεται η σύνδεση ανά 8s, προβάλλουμε ένα Toast κάθε φορά που ελέγχεται δηλαδή στην *onReceive()* με μήνυμα που δείχνει ότι υπάρχει ή δεν υπάρχει σύνδεση μέχρι τη τρέχουσα στιγμή.
```java
if(serviceManager.isNetworkAvailable())
    Toast.makeText(context, "Network Available", Toast.LENGTH_SHORT).show();
else
    Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show();
```

Έχουμε φροντίσει μετά το πάτημα του home button και την επαναφορά της εφαρμογής στο προσκήνιο η σύνδεση του android app με το java app να διατηρείται.

## Java app :computer:
Η java εφαρμογή αποτελείται από ένα αρχείο που υπολογίζει την εντροπία των δεδομένων το οποίο και περιέχει την main της εφαρμογής, το αρχείο ProbabilityState που μας δίνεται, το αρχείο ΚΝΝalgorithm το οποίο υλοποιεί τον αλγόριθμο Knn και στέλνει τα αποτελέσματα στον buffer, τα αρχεία Consumer και Producer που περιέχουν τα συγχρονισμένα thread και τον buffer και τέλος το αρχείο MQTTclient που περιέχει τον client και τα απαραίτητα για την υλοποίηση του.
![alt text](https://anapgit.scanlab.gr/anniepap/cerebro/blob/master/sort_verification.JPG)

Ξεκινάμε την εφαρμογή τρέχοντας την Entropy.java, η οποία στέλνει και δέχεται δεδομένα απο την ProbabilityState και δημιουργεί μια κλάση KNNalgorithm, στην οποία αρχικοποιούμε τον MQTTclient και τους producer, consumer.

Από τα δεδομένα αφαιρούμε εκείνα των οποίων οι αισθητήρες είχαν QoS κάτω απο 4, έτσι πετυχαίνουμε efficiency 73.4% με κ=7.
![alt text](https://anapgit.scanlab.gr/anniepap/cerebro/blob/master/efficiency.JPG)

Όταν όλα τα δεδομένα έχουν σταλθεί στον buffer μέσω της ΚΝΝ στέλνουμε ένα τελικό μήνυμα στον buffer "finish", το οποίο όταν σταλθεί στον client κάνει disconnect.


## Επικοινωνία :speech_balloon:
Για την επικοινωνία μεταξύ των δύο εφαρμογών έχουν χρησιμοποιηθεί οι αντίστοιχες Java και Android βιβλιοθήκες του MQTT, καθώς και ο mosquitto broker. Δημιουργούνται 2 topics, το commands στο οποίο είναι publisher η java εφαρμογή και το frequency στο οποίο κάνει publish η android εφαρμογή. Συγκεκριμένα, οι εντολές που παράγει το java app είναι:
```
turn On
turn Off
finish
```
Όπου με την εντολή finish τερματίζουν τα thread των producer, consumer και σταματά η επικοινωνία με την android εφαρμογή.

## Build :hammer:
Για το build και του android app καθώς και του java app χρησιμοποιήθηκε το gradle, με σκοπό να εξασφαλίσουμε ότι το κατέβασμα των απαραίτητων βιβλιοθηκών του ΜQTT θα γίνεται αυτόματα. Συγκεκριμένα, το κατέβασμα γίνετα χάρη στα αρχεία *build.gradle(Module: app)* και *build.gradle(Module: java_app)* στο android και java app αντιστοίχως. Παρακάτω φαίνεται η πηγή των dependencies, το maven, καθώς και συγκεκριμένα οι εκδόσεις που χρησημοποιήθηκαν.
```groovy
repositories {
    mavenCentral()
    maven {
        url "https://repo.eclipse.org/content/repositories/paho-snapshots/"
    }
}
dependencies {
    compile 'org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.1.0'
    compile 'org.eclipse.paho:org.eclipse.paho.android.service:1.1.1'
}
```

## Εκτέλεση :sound:
Έχοντας ήδη ανοιχτό τον mosquitto broker, ανοίγουμε πρώτα την android εφαρμογή και φροντίζουμε το κινητό μας να είναι συνδεδεμένο στο ίδιο δίκτυο με τον υπολογιστή όπου θα εκτελεστεί η java εφαρμογή. Στη συνέχεια εισάγουμε μέσω της κατάλληλης επιλογής του μενού την IP και port 1883. Έτσι η εφαρμογή εγγράφεται ως subscriber στο topic commands.
Έπειτα εκτελούμε την java εφαρμογή, η οποία τώρα θα μπορεί να κάνει pubish στο topic commands τα αντίστοιχα μηνύματα για τις on/off εντολές, ενώ παράλληλα εγγράφεται και ως subcriber στο topic frequency. Έτσι η android εφαρμογή θα μπορεί πλέον να κάνει και publish τα αντίστοιχα μηνύματα για την αλλαγή της συχνότητας αποστολής των on/off εντολών.

## Δημιουργοί :mortar_board:
Αδαμοπούλου Δέσποινα 

Μπόκος Δημήτρης 

Παπαχρήστου Άννα


