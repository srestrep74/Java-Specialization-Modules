#!/bin/bash

echo "Running Cucumber Component Tests for Trainee Management..."
echo

echo "Cleaning previous test results..."
mvn clean

echo
echo "Running all component tests..."
mvn test -Dtest=TraineeComponentTestSuite -Dspring.profiles.active=test

echo
echo "Tests completed. Check the reports in target/cucumber-reports/" 