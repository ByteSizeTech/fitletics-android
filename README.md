# Fitletics-android

## About
Fitletics is our capstone project which is a framework that helps the users keep up with their fitness goals. This is done by a [web component](https://github.com/ByteSizeTech/fitletics-web) that helps with the real-time tracking of exercises as well as an Android app that recommends workouts based on the body type and current fitness level of the user which then provides analytics on the past sessions <br>

This is the android app component that behaves sort of the companion for the user. This is where the user will be asked to sign into their account. The database and authentication was handled using Firebase. After signing in, the user user will enter the app which has 4 screens:
<ul>
	<li> <b>Dashboard</b> - This is where the user can see favorited analytics as well as general fitness metrics </li>
	<li> <b>Workouts</b> - This is where the user can create new custom workouts, browse and begin recommended or saved workouts along with the ability to send and receive workouts from other user </li>
	<li> <b>Analytics</b> - This screen allows the user to check analytics of past sessions per muscle category (not implemented) or per exercise </li>
	<li> <b>Settings</b>- This is where the user may re-take the baseline test or re-scan their body type. The user can change their information as well as logout from here  </li>
</ul>

## Getting Started
<b>Clone the repository </b>
``` 
git clone https://github.com/ByteSizeTech/fitletics-android.git
```

## Directory Structure
```
    ...
    ├── activities                  
        ├── analytics				# Contains analytics activities
        ├── appentry         			# Loggin/SignUp
        ├── baselinetest         		# Activities related to the BLT
        ├── bodyanalysis         		# Activities related to the BA
        ├── main         			# Main activity
        └── web         			# Activities that connect to the website
            ├── connect				# Activity that establishes connection
            ├── session				# Activities that have ongoing sessions
            └── workouts			# Pre-session activities that go through a workout
    ├── workout         			# App standalone workout activities
    ├── adapters                    		# Adapter classes
    ├── fragments                   
        ├── dialogs				# app dialog classes
        └── homepage				# homescreen fragments used by MainActivity
    └── models
        ├── misc				# Contains miscellaneous support files 
        ├── support				# Contains classes
        └── utils				# utility classes                             	
    ...
```	
