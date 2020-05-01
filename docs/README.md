# TDIDoctor Documentation
TDIDoctor is an Android solution to the modern problem of low-speed pre-ignition in vehicles with turbocharged gasoline direct-injection engines. TDIDoctor connects to Bluetooth OBD-II adapters to read and display vehicle data in real time while driving in order to provide more insight into vehicle health and prevent drivers from damaging their vehicles. 

## Sections
* System Vision
* Instructions
* Stakeholder Analysis
* GUI Mockups
* Testing and Analysis Results

## System Vision
Gasoline turbocharged direct-injection (TDI) engines are on the forefront of automotive technology and are being used by more and more automobile manufacturers with time and in a more frequent manner. TDI engines are fantastic due to their low cost to manufacture, high power density, reduced greenhouse gas emissions, and reliability. There is one downfall of TDI engines, however, and that is a phenomenon called low-speed pre-ignition (LSPI). When a driver has their vehicle at a high rate of speed (roughly > 45 MPH) and a low engine speed (roughly < 3,500 RPM), pressing on the accelerator hard will lead to the turbocharger loading too much pressure into the engine and causing ignition to occur before it is supposed to. This can cause damage ranging from minor noise to irreparable engine damage. While this phenomenon is easily avoidable, many drivers are either unaware of the bounds to cause it or do not know about the phenomenon at all. TDIDoctor is an application to both spread awareness on LSPI and prevent its occurrence in TDI vehicles, as well as provide more insight into a user's driving patterns and vehicle health. TDIDoctor utilizes Bluetooth technology to display vehicle data live while driving, and warns the user if they are in the bounds of LSPI occurring. 

## Instructions
Using TDIDoctor is very simple. These are the steps you must take to use the application:
1. Download and install TDIDoctor onto an Android device
2. Ensure your Bluetooth OBD-II adapter is plugged into your vehicle's diagnostic port
3. Connect to the adapter via your device settings
4. Open TDIDoctor
5. Click the "Initialize Bluetooth Connection" button
6. Begin tracking data!
7. Stop tracking data whenever your trip is done
8. Go to the Extras tab to review your speed and RPM graphs, as well as vehicle trouble codes
	9. Once in the Extras tab, press the "Load Data" button
	10. Both graphs are scalable and scrollable
	11. Press the "Display Trouble Codes" button to view vehicle trouble codes (if there are any)

## Stakeholder Analysis
TDIDoctor has a fairly limited but also wide-reaching audience. The application appeals to: 
- Automotive manufacturers
	- TDIDoctor ensures that their OBD-II port transmits data as it should (even in vehicles without TDI engines)
	- TDIDoctor can be used to record acceleration rates, shift times in automatic vehicles, and many more
- Car Enthusiasts
	- Many people with TDI-equipped vehicles are car enthusiasts due to its high power density capabilities. Car enthusiasts are much more likely to be aware of LSPI than passive vehicle owners, and this application could prevent enthusiasts from damaging their vehicle from highway driving, track days, etc.
- TDI Drivers
	- Even drivers of TDI-equipped vehicles that are not car enthusiasts will get much out of this application. The application will allow them to be more connected with their vehicle, as well as increase their vehicle's longevity like previously stated. 
## Testing and Analysis Results
