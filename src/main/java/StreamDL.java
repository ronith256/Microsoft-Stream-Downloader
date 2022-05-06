import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import net.bramp.ffmpeg.*;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.job.FFmpegJob;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import net.bramp.ffmpeg.progress.Progress;
import net.bramp.ffmpeg.progress.ProgressListener;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.GeckoDriverService;

import javax.swing.*;

public class StreamDL extends Config {
    StreamDL(String url, String userName, String password){
        GUI.jp.setVisible(true);
        GUI.statusLabel.setText("Downloading");
        try{
        getHttpRequests(url, userName, password);}catch(Exception ignored){};
    }

    public void getHttpRequests(String url, String userName, String password) throws IOException, InterruptedException {
        writeConfig();
        String driverLoc = System.getProperty("user.dir");
        driverLoc = driverLoc + "\\config\\geckodriver.exe";

        System.setProperty("webdriver.gecko.driver", driverLoc);
        GeckoDriverService service = null;
        Map<String, String> map = new HashMap<String, String>();
        map.put("MOZ_LOG", "timestamp,sync,nsHttp:4");
        File tempFile = File.createTempFile("mozLog", ".txt");
        map.put("MOZ_LOG_FILE", tempFile.getAbsolutePath());

        GeckoDriverService.Builder builder = new GeckoDriverService.Builder();
        service = builder.usingAnyFreePort()
                .withEnvironment(map)
                .build();
        service.start();
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--headless");
        WebDriver driver = new FirefoxDriver(service,options);

        driver.get(url);
        Thread.sleep(1000);
        driver.findElement(By.id("i0116")).sendKeys(userName);
        GUI.statusLabel.setText("Logging in");
        driver.findElement(By.id("idSIButton9")).click();
        Thread.sleep(3000);
        driver.findElement(By.id("i0118")).sendKeys(password);
        driver.findElement(By.id("idSIButton9")).click();
        Thread.sleep(100);
        driver.findElement(By.id("idSIButton9")).click();
        Thread.sleep(10000);
        driver.findElement(By.cssSelector(".vjs-big-play-button")).click();
        driver.quit();
        String filePath = tempFile.getAbsolutePath() + ".moz_log";
        System.out.println(filePath);
        parseUrl(filePath);
    }
    private void parseUrl(String filePath){
        GUI.statusLabel.setText("Finding URL");
        String line = "";
        String contents = "";
        try {
            contents = FileUtils.readFileToString(new File(filePath));
        } catch(Exception ignored){};
        try{
        Scanner ab = new Scanner(contents);
        line = "";
        while(ab.hasNextLine()){
            line = ab.next();
            if(line.contains("videomanifest?")){
                line = line.substring(line.indexOf("uri=")+4, line.indexOf(","));
                break;
            }
        }} catch (Exception ignored){};
        try{
        downloadVideo(line);}catch (Exception ignored){};
    }

    private void downloadVideo(String url) throws IOException {
        GUI.statusLabel.setText("");
        FFmpeg ffmpeg = new FFmpeg("config/ffmpeg.exe");
        FFprobe ffprobe = new FFprobe("config/ffprobe.exe");

        FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);

        FFmpegProbeResult in = ffprobe.probe(url);

        FFmpegBuilder builder = new FFmpegBuilder()
                .setInput(in) // Or filename
                .addOutput(GUI.fileName)
                .done();

        FFmpegJob job = executor.createJob(builder, new ProgressListener() {

            final double duration_ns = in.getFormat().duration * TimeUnit.SECONDS.toNanos(1);

            @Override
            public void progress(Progress progress) {

                double percentage = progress.out_time_ns / duration_ns;
                double percent = percentage*100;

                if((int)percent >= 100){
                    GUI.jp.setVisible(false);
                } else {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        GUI.jp.setValue((int)percent);
                    }
                });}

                // Print out interesting information about the progress
                System.out.println(String.format(
                        "[%.0f%%] status:%s frame:%d time:%s ms fps:%.0f speed:%.2fx",
                        percentage * 100,
                        progress.status,
                        progress.frame,
                        FFmpegUtils.toTimecode(progress.out_time_ns, TimeUnit.NANOSECONDS),
                        progress.fps.doubleValue(),
                        progress.speed
                ));
            }
        });
        job.run();
    }
}
