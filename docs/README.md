# TDIDoctor Documentation
TDIDoctor is an Android solution to the modern problem of low-speed pre-ignition in vehicles with turbocharged gasoline direct-injection engines. TDIDoctor connects to Bluetooth OBD-II adapters to read and display vehicle data in real time while driving in order to provide more insight into vehicle health and prevent drivers from damaging their vehicles. 

## Sections
* System Vision
* Instructions
* Stakeholder Analysis
* GUI Mockups
* Testing and Analysis Results
* Viewing JavaDoc

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
Testing this project alone is difficult. Much of my testing involved me sitting alone in my car with my computer and test device for hours at a time, restarting my car often to test code changes. These are the results I got from testing, ordered chronologically:
* My original test device screen was not fully operational
* Updates would take so long to return that they were not useful
* Asynchronous Bluetooth transmission was necessary to not block the UI and to update continuously
* Turbocharger pressure reading is not built into OBD-II
	* I had to use a formula to determine turbocharger pressure. The formula is: (Turbocharger pressure) = (intake manifold pressure) - (absolute air pressure)
* My original LSPI detection algorithm bounds were not safe enough to prevent the issue
* Storing vehicle data as raw text was not the proper way to go
	* This is why I decided to use a SQLite database to manage data storage
* Creating graphs to display data in vanilla Android is nearly impossible
* OBD-II trouble code PIDs were not returning in a properly formatted string
* The extra, non-essential application pieces I wanted to incorporate were not feasible by my presentation date
* I had to create documentation!

## Viewing JavaDoc
If you would like to view this project's JavaDocs, you must do the following:
1. Go to the GitHub link at the bottom of this page
2. Download the project using your preferred Git method
3. Open the repository in a file browser
4. Open the "JavaDoc" folder
5. Open index.html in your preferred browser
