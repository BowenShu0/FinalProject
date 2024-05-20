# Weather App

## Overview

This Weather App provides current weather information for a specified city. It fetches data from the OpenWeatherMap API and displays the temperature, humidity, wind speed, and other relevant weather details. Users can enter a city name to get the weather information, and if there's an error (such as an invalid city name), an error message is displayed below the search box while retaining the current city data on the screen.

## Features

- Fetches real-time weather data from the OpenWeatherMap API.
- Displays temperature, minimum and maximum temperature, humidity, wind speed, sunrise, and sunset times.
- Shows an error message below the search box if the API call fails.
- Retains the previous city's weather data on the screen even if there's an error in fetching new data.

## Figma Design

**Link:** https://www.figma.com/design/nAhlqbQjflXcrcpeJSpc4R/Weather-App?node-id=0%3A1&t=ARBCIg6GqeQZdNHZ-1

## How It Works

1. **Initial Load**: The app initializes with a default city, "Seattle, US", and fetches its weather data on launch.
2. **User Input**: Users can enter a city name in the search box and click the submit button.
3. **Fetching Data**: The app fetches weather data for the entered city from the OpenWeatherMap API.
4. **Display Data**: If the API call is successful, the app updates the screen with the new weather data.
5. **Error Handling**: If the API call fails, an error message is displayed below the search box, and the previous city's weather data remains on the screen.

## Challenges Faced

- **API Integration**: Integrating the OpenWeatherMap API and handling the JSON response was a bit challenging, especially managing different possible errors.
- **UI Updates**: Ensuring the UI updates correctly after fetching new data or encountering an error required careful management of view visibility and content updates.
- **Error Handling**: Implementing robust error handling to provide informative messages to the user while retaining the previous valid data was a key challenge.

## Hours Worked

- **Planning and Design**: 2 hours
- **UI Development**: 3 hours
- **API Integration and Data Parsing**: 4 hours
- **Error Handling Implementation**: 2 hours
- **Testing and Debugging**: 3 hours
- **Documentation**: 1 hour

**Total**: 15 hours

## Acknowledgments

- **OpenWeatherMap**: For providing the weather API.
- **Android Documentation**: For detailed documentation and examples that helped in building the app.
- **Stack Overflow Community**: For providing solutions and guidance on various development challenges.