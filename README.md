## Overview
Simple to-do list using Google API for "High-Level Programming Languanges" on AGH UST.

## Technologies
Project is created with:
* Java SE 17
* Gradle 7.1
* JavaFX 17.0.1
* Tasks API v1-rev20210709-1.32.1
* google-oauth-client 1.32.1
* MySQL Connector Java 5.1.13

## Authorization
On a startup the application will ask to choose user's Google account to access Google Tasks.

<img src="/images/oauth2.png" alt="oauth2" width="300" height="400">

After that the access token will be generated and there will be no need for re-authorization.

## Usage
The application allows tu use the functionality of Google Tasks and extends it with prioritize tasks, sort by priority or by date and show remaining time to completion. Every operation of inserting, updating or deletion task or tasks list will be uploaded to Google Tasks application.

<img src="/images/gui.png" alt="gui" width="700" height="400">
