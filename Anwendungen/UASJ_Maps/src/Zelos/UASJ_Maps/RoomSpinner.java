/*
This file is part of UASJ-Maps.

UASJ-Maps is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

UASJ-Maps is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with UASJ-Maps. If not, see http://www.gnu.org/licenses/
*/

package Zelos.UASJ_Maps;

import android.content.Context;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;

public class RoomSpinner {

    private Context context; // Context von Activity Klasse
    private Spinner spHouse; // DropDownMenü für die Hausnummer
    private Spinner spFloor; // DropDownMenü für die Etagennummer
    private Spinner spRoom; // DropDownMenü für die Raumnummer + ggf. Zusatzinformationen z.B. HS 2

    /**Klasse zur Verwaltung der 3 Spinner, die zur Eingabe eines Raumes benötigt werden
     * je nachdem welches Haus ausgewählt wird, werden die verfügbaren Etagen geändert
     * und je nachdem welche Etage ausgewählt wird, werden die verfügbaren Räume geändert
     * @param _context Context von Activity Klasse
     * @param _spHouse Spinner für HausID von xml
     * @param _spFloor Spinner für EtagenID von xml
     * @param _spRoom Spinner für RaumID von xml
     */
    public RoomSpinner(Context _context, Spinner _spHouse, Spinner _spFloor, Spinner _spRoom) {
        context = _context; // Context merken
        spHouse = _spHouse; // Hausspinner merken
        spFloor = _spFloor; // Etagenspinner merken
        spRoom = _spRoom; // Raumspinner merken

        spHouse.setAdapter(createAdapter(R.array.campus)); // Adapter für Campus setzen
        spHouse.setOnItemSelectedListener(createHouseListener()); // Listener setzen
    }

    /**@return RaumID als String z.B. '%05.02.01,%'
     */
    public String getString() {
        String shortenedRoom = (String) spRoom.getSelectedItem(); // kompletter angezeigter String des Raums
        int x = shortenedRoom.indexOf(" "); // Index des Leerzeichens herausfinden; gibt -1 zurück, wenn kein Leerzeichen vorhanden
        if (-1 != x) // wenn Leerzeichen vorhanden
            shortenedRoom = shortenedRoom.substring(0, x); // Leerzeichen und alles dahinter abschneiden
        return "'%" + spHouse.getSelectedItem() + "." + spFloor.getSelectedItem() + "." + shortenedRoom + ",%'"; // HausID.EtagenID.RaumID
    }

    /** setzt grafische Darstellung und wählt String Array aus was angezeigt werden soll
     * @param id ID des String Arrays angeben
     * @return Adapter für Spinner
     */
    private ArrayAdapter<CharSequence> createAdapter(int id) {
        ArrayAdapter<CharSequence> AA = ArrayAdapter.createFromResource(context, id, android.R.layout.simple_spinner_item); // String Array auswählen
        AA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // grafische Anzeige definieren
        return AA;
    }

    /** erstellt Listener für Haus Spinner
     * @return OnItemSelectedListener, der ausgeführt wird, wenn ein Element des DropDownMenüs ausgewählt wird
     */
    private OnItemSelectedListener createHouseListener() {
        return new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                int houseID;
                OnItemSelectedListener floorListener;
                switch (arg2) { // Welches Element wurde ausgewählt?
                case 1: // Haus 02
                    houseID = R.array.house02;
                    floorListener = createFloorListener_House02();
                    break;
                case 2: // Haus 03
                    houseID = R.array.house03;
                    floorListener = createFloorListener_House03();
                    break;
                case 3: // Haus 04
                    houseID = R.array.house04;
                    floorListener = createFloorListener_House04();
                    break;
                case 4: // Haus 05
                    houseID = R.array.house05;
                    floorListener = createFloorListener_House05();
                    break;
                default: // Haus 01
                    houseID = R.array.house01;
                    floorListener = createFloorListener_House01();
                    break;
                }

                spFloor.setAdapter(createAdapter(houseID)); // Adapter für Etagen setzen
                spFloor.setOnItemSelectedListener(floorListener); // Listener setzen
            }

            public void onNothingSelected(AdapterView<?> arg0) { // unused
            }
        };
    }

    /** erstellt Listener für Etagen Spinner von Haus 01
     * @return OnItemSelectedListener, der ausgeführt wird, wenn ein Element des DropDownMenüs ausgewählt wird
     */
    private OnItemSelectedListener createFloorListener_House01() {
        return new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                int floorID;
                switch (arg2) { // Welches Element wurde ausgewählt?
                case 1: // Etage 00
                    floorID = R.array.house01_floor00;
                    break;
                case 2: // Etage 01
                    floorID = R.array.house01_floor01;
                    break;
                case 3: // Etage 02
                    floorID = R.array.house01_floor02;
                    break;
                case 4: // Etage 03
                    floorID = R.array.house01_floor03;
                    break;
                case 5: // Etage 04
                    floorID = R.array.house01_floor04;
                    break;
                default: // Etage -1
                    floorID = R.array.house01_floor_1;
                    break;
                }

                spRoom.setAdapter(createAdapter(floorID)); // Adapter für Räume setzen
            }

            public void onNothingSelected(AdapterView<?> arg0) { // unused
            }
        };
    }

    /** erstellt Listener für Etagen Spinner von Haus 02
     * @return OnItemSelectedListener, der ausgeführt wird, wenn ein Element des DropDownMenüs ausgewählt wird
     */
    private OnItemSelectedListener createFloorListener_House02() {
        return new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                int floorID;
                switch (arg2) { // Welches Element wurde ausgewählt?
                case 1: // Etage 01
                    floorID = R.array.house02_floor01;
                    break;
                case 2: // Etage 02
                    floorID = R.array.house02_floor02;
                    break;
                case 3: // Etage 03
                    floorID = R.array.house02_floor03;
                    break;
                case 4: // Etage 04
                    floorID = R.array.house02_floor04;
                    break;
                default: // Etage 00
                    floorID = R.array.house02_floor00;
                    break;
                }

                spRoom.setAdapter(createAdapter(floorID)); // Adapter für Räume setzen
            }

            public void onNothingSelected(AdapterView<?> arg0) { // unused
            }
        };
    }

    /** erstellt Listener für Etagen Spinner von Haus 03
     * @return OnItemSelectedListener, der ausgeführt wird, wenn ein Element des DropDownMenüs ausgewählt wird
     */
    private OnItemSelectedListener createFloorListener_House03() {
        return new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                int floorID;
                switch (arg2) { // Welches Element wurde ausgewählt?
                case 1: // Etage 00
                    floorID = R.array.house03_floor00;
                    break;
                case 2: // Etage 01
                    floorID = R.array.house03_floor01;
                    break;
                case 3: // Etage 02
                    floorID = R.array.house03_floor02;
                    break;
                case 4: // Etage 03
                    floorID = R.array.house03_floor03;
                    break;
                default: // Etage -1
                    floorID = R.array.house03_floor_1;
                    break;
                }

                spRoom.setAdapter(createAdapter(floorID)); // Adapter für Räume setzen
            }

            public void onNothingSelected(AdapterView<?> arg0) { // unused
            }
        };
    }

    /** erstellt Listener für Etagen Spinner von Haus 04
     * @return OnItemSelectedListener, der ausgeführt wird, wenn ein Element des DropDownMenüs ausgewählt wird
     */
    private OnItemSelectedListener createFloorListener_House04() {
        return new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                int floorID;
                switch (arg2) { // Welches Element wurde ausgewählt?
                case 1: // Etage 00
                    floorID = R.array.house04_floor00;
                    break;
                case 2: // Etage 01
                    floorID = R.array.house04_floor01;
                    break;
                case 3: // Etage 02
                    floorID = R.array.house04_floor02;
                    break;
                case 4: // Etage 03
                    floorID = R.array.house04_floor03;
                    break;
                default: // Etage -1
                    floorID = R.array.house04_floor_1;
                    break;
                }

                spRoom.setAdapter(createAdapter(floorID)); // Adapter für Räume setzen
            }

            public void onNothingSelected(AdapterView<?> arg0) { // unused
            }
        };
    }

    /** erstellt Listener für Etagen Spinner von Haus 05
     * @return OnItemSelectedListener, der ausgeführt wird, wenn ein Element des DropDownMenüs ausgewählt wird
     */
    private OnItemSelectedListener createFloorListener_House05() {
        return new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                int floorID;
                switch (arg2) { // Welches Element wurde ausgewählt?
                case 1: // Etage 00
                    floorID = R.array.house05_floor00;
                    break;
                case 2: // Etage 01
                    floorID = R.array.house05_floor01;
                    break;
                case 3: // Etage 02
                    floorID = R.array.house05_floor02;
                    break;
                case 4: // Etage 03
                    floorID = R.array.house05_floor03;
                    break;
                default: // Etage -1
                    floorID = R.array.house05_floor_1;
                    break;
                }

                spRoom.setAdapter(createAdapter(floorID)); // Adapter für Räume setzen
            }

            public void onNothingSelected(AdapterView<?> arg0) { // unused
            }
        };
    }
}