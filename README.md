A feladatleírás a félévi projektre:
A feladat a klasszikus torpedó játék implementálása, amit hálózaton keresztül játszhat két fél egymás ellen. A megvalósításhoz szerver-kliens architektúrát kell használni, a szerver implementálja a játék logikáját és kezeli a kliensek csatlakozását, a játékosok regisztráció és bejelentkezés után játszhatnak egy másik elérhető játékos ellen. 

A feladatnál elvárás a forduló és minimálisan működő kód. Ezek hiányában a munka értékelhetetlen (0 pont). 

A szerver feladatai
Tárolja a regisztrált felhasználók adatait 
Kezeli a futó játékokat 
Tárolja a korábbi játékok eredményeit, statisztikáit 
Lekérdezhetők a beállított és elindított játékok, amik közül lehet választani és csatlakozni 
A kliensek csatlakoznak a szerverhez és a játék lebonyolítását a szerveren keresztül végzik 
A kliens feladatai
Interfész a szerver funkciók eléréséhez 
Grafikus felhasználói felület készítése JavaFx segítségével
Felhasználói regisztrációs felület, bejelentkezés 
Játék kiválasztása a szerveren elindított, de ellenféllel még nem rendelkező játékok közül 
Játék felületének megjelenítése 
Játék beállításainak módosítása 
A felhasználó játék statisztikáinak megjelenítése 
Felhasználó beállításainak módosításához felület 
Játékszabályok
Játékmenet a klasszikus torpedó szerint: 
Torpedó (játék) – Wikipédia (wikipedia.org)
Legyen állítható a pálya mérete 
A pályaméret szerint legyenek a hajó típusok (hosszúság) választhatók 
Amire érdemes figyelni
Játékok állapotkezelése 
Hálózati kapcsolat hibájának kezelése  
Helyes kivételkezelés és szálkezelés – ne fagyjon le a kliens 
Extra funkciók
Tengeralattjáró: a játék során X alkalommal (táblamérettől függően) megváltoztatható a helye, de a kapott találatokat megőrzi 
Chat implementálása üzenet küldéshez a két játékos között 
Játék időre: ha lejár az idő, az győz, aki kevesebb találatot kapott 
