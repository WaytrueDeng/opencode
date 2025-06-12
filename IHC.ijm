
print("Calling getDirectory");
var rootDirectory = getDirectory("Choose a Root Directory");
print("Directory chosen: " + rootDirectory);
// Function to read threshold values from a file

// Choose the root directory


// Function to process images in a given directory
function processImages(directory) {
    var list = getFileList(directory);
    var results = "File Name,Area,Mean,Indensity,%Area\n";

    for (var i = 0; i < list.length; i++) {
        var file = list[i];
        var fullPath = directory + file;

        if (File.isDirectory(fullPath)) {
            // Recursive call to process subfolders
            processImages(fullPath + "/");
        } else if (endsWith(file, ".tif")) {
            // Read thresholds from the containing directory of the .tif file

            // Construct the full file path
            var filePath = fullPath;

            // Open the image
            open(filePath);

            // Ensure the image is RGB type (this step may depend on your actual use case)

            // Perform Colour Deconvolution analysis
            run("Colour Deconvolution", "vectors=[H DAB]");

            // Get the name of the new window (e.g., 1.tif -> 1.tif-(Colour1))
            var windowTitle = file + "-(Colour_2)";

            // Select the fibrosis channel window
            selectWindow(windowTitle);
run("Calibrate...", "function=[Uncalibrated OD] unit=[Gray Value] text1= text2= show");
            // Set thresholds and measure fibrosis area
    setThreshold(0, 170,"raw");
            run("Measure");
            var Area = getResult("Area", 0);
            var Mean = getResult("Mean", 0);
            var IntDen = getResult("IntDen", 0);
            var pArea = getResult("%Area", 0);

            // Clear results table
            run("Clear Results");
            
            // Append results
            results += file + "," + Area + "," + Mean + "," + IntDen + "," + pArea+ "\n";

            // Close all open windows
            close("*");
        }
    }

    // Save results to CSV file
    var saveResultsPath = directory + "IHC.csv";
    File.saveString(results, saveResultsPath);

    // Print completion message
    print("Processing complete! Results saved to: " + saveResultsPath);
}

// Start processing from the root directory

run("Set Measurements...", "area mean min area_fraction limit display redirect=None decimal=3");
processImages(rootDirectory);
