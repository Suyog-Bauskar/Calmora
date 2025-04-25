/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

const {onSchedule} = require("firebase-functions/v2/scheduler");
const {onRequest} = require("firebase-functions/v2/https");
const {onDocumentCreated, onDocumentUpdated} = require("firebase-functions/v2/firestore");
const admin = require('firebase-admin');
const logger = require("firebase-functions/logger");

// Initialize Firebase Admin SDK
admin.initializeApp();

/**
 * Helper to get current week identifier
 */
function getCurrentWeekId() {
  const now = new Date();
  const year = now.getFullYear();
  
  // Create a new date object for the first day of the year
  const firstDayOfYear = new Date(year, 0, 1);
  
  // Calculate the week number
  // getDay() returns the day of week, where 0 is Sunday and 6 is Saturday
  const firstDayOfWeekDay = firstDayOfYear.getDay();
  
  // Calculate days since first day of year, adjusted by day of week
  const daysSinceFirstDay = Math.floor((now - firstDayOfYear) / (24 * 60 * 60 * 1000)) + firstDayOfWeekDay;
  
  // Calculate the week number
  const weekNumber = Math.ceil(daysSinceFirstDay / 7);
  
  return `week_${year}_${weekNumber}`;
}

/**
 * Scheduled function that runs weekly to reset the weekly data
 * This runs at the start of a new week (Monday 00:01 UTC)
 */
exports.resetWeeklyAppUsage = onSchedule({
  schedule: "1 0 * * 1", // Every Monday at 00:01 UTC
  timeZone: "UTC"
}, async (event) => {
  try {
    logger.info("Starting weekly app usage reset");
    
    // Generate the new week ID
    const newWeekId = getCurrentWeekId();
    logger.info(`New week ID: ${newWeekId}`);
    
    // Get all users
    const usersSnapshot = await admin.firestore().collection("Users").get();
    
    // Process each user
    for (const userDoc of usersSnapshot.docs) {
      const userData = userDoc.data();
      const userId = userDoc.id;
      
      const updateData = {
        // Reset each day of the week
        "day_1": 0,
        "day_2": 0,
        "day_3": 0,
        "day_4": 0,
        "day_5": 0,
        "day_6": 0,
        "day_7": 0,
        // Reset current week usage
        "currentWeekUsage": 0,
        // Update the week identifier
        "currentWeekId": newWeekId
      };
      
      // Update user document
      await admin.firestore().collection("Users").doc(userId).update(updateData);
      
      logger.info(`Reset weekly data for user ${userId}`);
    }
    
    logger.info("Weekly app usage reset completed successfully");
    return null;
  } catch (error) {
    logger.error("Error in weekly app usage reset:", error);
    return null;
  }
});

/**
 * Test function to manually trigger the weekly reset
 */
exports.manualTriggerWeeklyReset = onRequest({
  cors: true
}, async (req, res) => {
  try {
    logger.info("Manually triggering weekly app usage reset");
    
    // Generate the new week ID
    const newWeekId = getCurrentWeekId();
    logger.info(`New week ID: ${newWeekId}`);
    
    // Get all users
    const usersSnapshot = await admin.firestore().collection("Users").get();
    
    // Process each user
    for (const userDoc of usersSnapshot.docs) {
      const userData = userDoc.data();
      const userId = userDoc.id;
      
      const updateData = {
        // Reset each day of the week
        "day_1": 0,
        "day_2": 0,
        "day_3": 0,
        "day_4": 0,
        "day_5": 0,
        "day_6": 0,
        "day_7": 0,
        // Reset current week usage
        "currentWeekUsage": 0,
        // Update the week identifier
        "currentWeekId": newWeekId
      };
      
      // Update user document
      await admin.firestore().collection("Users").doc(userId).update(updateData);
      
      logger.info(`Reset weekly data for user ${userId}`);
    }
    
    logger.info("Manual weekly app usage reset completed successfully");
    res.status(200).send({ success: true, message: "Weekly reset completed" });
  } catch (error) {
    logger.error("Error in manual weekly app usage reset:", error);
    res.status(500).send({ success: false, error: error.message });
  }
});

// Create and deploy your first functions
// https://firebase.google.com/docs/functions/get-started

// exports.helloWorld = onRequest((request, response) => {
//   logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });
