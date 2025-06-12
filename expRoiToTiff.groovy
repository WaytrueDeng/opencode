import qupath.lib.regions.RegionRequest
import qupath.lib.images.writers.ImageWriterTools

try {
    // 获取当前图像数据
    def imageData = getCurrentImageData()
    def server = imageData.getServer()
    def imagePath = server.getPath()
    def imageName = server.getMetadata().getName()

    // 打印调试信息
    print "Image path: " + imagePath
    print "Image name: " + imageName

    // 清理图像路径
    // 去掉 BioFormatsImageServer: file:\ 和 [--series, 0]
    def cleanedImagePath = imagePath.replaceAll(/BioFormatsImageServer: file:/, "").replaceAll(/\[--series, \d+\]$/, "")
    // 确保路径分隔符为Windows格式
    def filePath = cleanedImagePath.replace("/", "\\")
    def imageFile = new File(filePath)
    def imageDir = imageFile.getParentFile()
    print "Image directory: " + imageDir

    // 创建region文件夹和图像名文件夹
    def regionDir = new File(imageDir, "region")
    print "Attempting to create region directory: " + regionDir.getAbsolutePath()

    if (!regionDir.exists()) {
        if (regionDir.mkdirs()) {
            print "Created region directory: " + regionDir
        } else {
            throw new IOException("Failed to create region directory: " + regionDir)
        }
    } else {
        print "Region directory already exists: " + regionDir
    }

    // 清理图像名称，去掉 .vsi 后面的部分
    def baseImageName = imageName.replaceAll(/(\.[^.]+)$/, "") // 移除扩展名
    def safeImageName = baseImageName.replaceAll(/[^a-zA-Z0-9.\-]/, "_") // 替换非法字符
    def imageSpecificDir = new File(regionDir, safeImageName)
    print "Attempting to create image specific directory: " + imageSpecificDir.getAbsolutePath()

    if (!imageSpecificDir.exists()) {
        if (imageSpecificDir.mkdirs()) {
            print "Created image specific directory: " + imageSpecificDir
        } else {
            throw new IOException("Failed to create image specific directory: " + imageSpecificDir)
        }
    } else {
        print "Image specific directory already exists: " + imageSpecificDir
    }

    // 获取所有的ROI
    def rois = getAnnotationObjects().findAll { it.isAnnotation() }
    print "Number of ROIs: " + rois.size()

    // 循环遍历每个ROI并导出为TIF
    rois.eachWithIndex { roi, index ->
        def region = roi.getROI()
        def request = RegionRequest.createInstance(server.getPath(), 1, region)
        
        def outputFile = new File(imageSpecificDir, "ROI_${index + 1}.tif")
        print "Output file: " + outputFile

        try {
            ImageWriterTools.writeImageRegion(server, request, outputFile.getAbsolutePath())
            print "Successfully wrote: " + outputFile
        } catch (Exception e) {
            print "Failed to write: " + outputFile + " with error: " + e.message
        }
        
    }

    print 'ROI导出完成！'
} catch (Exception e) {
    print "Error: " + e.message
    e.printStackTrace()
}
