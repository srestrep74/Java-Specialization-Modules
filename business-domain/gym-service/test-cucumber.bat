@echo off
echo ========================================
echo Running Cucumber Component Tests
echo ========================================
echo.

echo Cleaning previous builds...
call mvn clean -q

echo.
echo Running Cucumber tests with test profile...
call mvn test -Dtest=TraineeComponentTestSuite -Dspring.profiles.active=test -q

echo.
echo ========================================
echo Tests completed!
echo Check reports in: target/cucumber-reports/
echo ========================================
pause 