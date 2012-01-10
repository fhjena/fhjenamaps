package Zelos.UASJ_Maps;

import android.content.Context;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;

public class RoomSpinner {
	
	private Context context;
	private Spinner spHouse;
	private Spinner spFloor;
	private Spinner spRoom;
	
	public RoomSpinner(Context _context, Spinner _spHouse, Spinner _spFloor, Spinner _spRoom) {
		context = _context;
		spHouse = _spHouse;
		spFloor = _spFloor;
		spRoom = _spRoom;
		
		spHouse.setAdapter(createAdapter(R.array.campus));
		spHouse.setOnItemSelectedListener(createHouseListener());
	}

	public String getString() {
		String shortenedRoom = (String) spRoom.getSelectedItem();
		int x = shortenedRoom.indexOf(" ");
		if (-1 != x)
			shortenedRoom = shortenedRoom.substring(0, x);
		return spHouse.getSelectedItem() + "." + spFloor.getSelectedItem() + "." + shortenedRoom; // TODO
	}
	
	private ArrayAdapter<CharSequence> createAdapter(int id) {
		ArrayAdapter<CharSequence> AA = ArrayAdapter.createFromResource(context, id, android.R.layout.simple_spinner_item); 
		AA.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		return AA;
	}

	private OnItemSelectedListener createHouseListener() {
		return new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				int houseID;
				OnItemSelectedListener floorListener;
				switch (arg2) {
				case 1:
					houseID = R.array.house02;
					floorListener = createFloorListener_House02();
					break;
				case 2:
					houseID = R.array.house03;
					floorListener = createFloorListener_House03();
					break;
				case 3:
					houseID = R.array.house04;
					floorListener = createFloorListener_House04();
					break;
				case 4:
					houseID = R.array.house05;
					floorListener = createFloorListener_House05();
					break;
				default:
					houseID = R.array.house01;
					floorListener = createFloorListener_House01();
					break;
				}
				
				spFloor.setAdapter(createAdapter(houseID));
				spFloor.setOnItemSelectedListener(floorListener);
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		};
	}

	private OnItemSelectedListener createFloorListener_House01() {
		return new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				int floorID;
				switch (arg2) {
				case 1:
					floorID = R.array.house01_floor01;
					break;
				case 2:
					floorID = R.array.house01_floor02;
					break;
				case 3:
					floorID = R.array.house01_floor03;
					break;
				case 4:
					floorID = R.array.house01_floor04;
					break;
				default:
					floorID = R.array.house01_floor_1;
					break;
				}
				
				spRoom.setAdapter(createAdapter(floorID));
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		};
	}

	private OnItemSelectedListener createFloorListener_House02() {
		return new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				int floorID;
				if (1==arg2)
					floorID = R.array.house02_floor03;
				else
					floorID = R.array.house02_floor02;
				
				spRoom.setAdapter(createAdapter(floorID));
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		};
	}

	private OnItemSelectedListener createFloorListener_House03() {
		return new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				int floorID;
				switch (arg2) {
				case 1:
					floorID = R.array.house03_floor01;
					break;
				case 2:
					floorID = R.array.house03_floor02;
					break;
				case 3:
					floorID = R.array.house03_floor03;
					break;
				default:
					floorID = R.array.house03_floor00;
					break;
				}
				
				spRoom.setAdapter(createAdapter(floorID));
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		};
	}

	private OnItemSelectedListener createFloorListener_House04() {
		return new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				int floorID;
				switch (arg2) {
				case 1:
					floorID = R.array.house04_floor00;
					break;
				case 2:
					floorID = R.array.house04_floor01;
					break;
				case 3:
					floorID = R.array.house04_floor02;
					break;
				case 4:
					floorID = R.array.house04_floor03;
					break;
				default:
					floorID = R.array.house04_floor_1;
					break;
				}
				
				spRoom.setAdapter(createAdapter(floorID));
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		};
	}

	private OnItemSelectedListener createFloorListener_House05() {
		return new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				int floorID;
				switch (arg2) {
				case 1:
					floorID = R.array.house05_floor00;
					break;
				case 2:
					floorID = R.array.house05_floor01;
					break;
				case 3:
					floorID = R.array.house05_floor02;
					break;
				case 4:
					floorID = R.array.house05_floor03;
					break;
				default:
					floorID = R.array.house05_floor_1;
					break;
				}
				
				spRoom.setAdapter(createAdapter(floorID));
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		};
	}
}
