import folium
import pandas as pd
import csv
import selenium
import os
import time
from selenium import webdriver

# This object holds the information needed to construct folium Markers
# Each Waypoint object may be used to construct multiple markers by 
# appending the relevant data to each list 
class Waypoint:
    def __init__(self):
        self.long_list = []
        self.lat_list = []
        self.name_list = []
        self.count_list = []

# Given Exercise 3's final results, constructs 4 unique folium maps depicting the location of the top 10 most visited stations for 
# each of the 4 6-hour timeslots.
# In order to produce a non-interactive image of these maps, we used Selenium along with a firefox driver in order to open the html maps
# take a screenshot from the browser and then save it locally.
station_coordinates = {}
with open('/home/user/Downloads/OneDrive_1_6-23-2023/dataset/2013-11 - Citi Bike trip data.csv', 'r', newline="") as csv_file:
    reader = csv.reader(csv_file, delimiter=',', quotechar='"')
    next(reader)
    # maps each station id to the station name and coordinates, in order for easier lookup later on
    for row in reader:
        station_name = row[4]
        station_id = row[3]
        lat = row[5]
        long = row[6]
        station_coordinates[station_id] = [station_name, lat, long]

with open ('/home/user/Downloads/final_results.csv', 'r', newline="") as results_file:
    reader = csv.reader(results_file, delimiter=',', quotechar='"')
    next(reader)
    waypoints = [Waypoint() for i in range(4)] #creates 4 Waypoint objects to hold the station markers of each timeslot (10 markers for each of the 4 timeslots)
    current_waypoint = waypoints[0]
    waypoint_id = "00:00 - 06:00"
    i = 0
    for row in reader:
        # On timeslot change, append the following station information to the next Waypoint
        if(row[0] != waypoint_id):
            i = i + 1
            current_waypoint = waypoints[i]
            waypoint_id = row[0]
        
        # Given the station id from the results, gets the station name and coordinates from the lookup map constructed earlier
        station_id = row[1].strip()
        current_waypoint.long_list.append(station_coordinates[station_id][2])
        current_waypoint.lat_list.append(station_coordinates[station_id][1])
        current_waypoint.name_list.append(station_coordinates[station_id][0])
        current_waypoint.count_list.append(int(row[2]))
    index = 0
    colours = ['red', 'green', 'blue', 'darkpurple']
    timeslots = ["00-06", "06-12", "12-18", "18-24"]
    # Each Waypoint holds the data to construct 10 Markers
    for waypoint in waypoints:
        m = folium.Map(location=[waypoint.lat_list[0], waypoint.long_list[0]], zoom_start=13, tiles="OpenStreetMap")

        # create a Pandas dataframe from the Waypoint's lists
        data = pd.DataFrame({
            'lon':waypoint.long_list,
            'lat':waypoint.lat_list,
            'name':waypoint.name_list,
            'value':waypoint.count_list
        }, dtype=str)
        print(data)

        # Create folium Markers from the dataframe
        for i in  range(0, len(data)):
            folium.Marker(icon=folium.Icon(icon='circle', color=colours[index]), prefix='fa',location=[data.iloc[i]['lat'], data.iloc[i]['lon']],
                    popup=data.iloc[i]['name'],).add_to(m)
        m.save('map_' + timeslots[index] + '.html')
        mapUrl = 'file:///home/user/Downloads/OneDrive_1_6-23-2023/dataset/' + 'map_' + timeslots[index] + '.html'
        driver = webdriver.Firefox(firefox_binary='/usr/bin/firefox')
        driver.get(mapUrl)

        time.sleep(5)
        driver.save_screenshot('map_' + timeslots[index] + '.png')
        driver.quit()
        index = index + 1