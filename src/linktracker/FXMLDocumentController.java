
package linktracker;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import linktracker.utils.*;
import linktracker.model.*;

/**
 *
 * @author Antonio Defez
 */
public class FXMLDocumentController implements Initializable {

    private ObservableList<String> currentWebpages;
    private ObservableList<String> currentLinks;
    private List<WebPage> webpages;
    private Label label;
    @FXML
    private ListView<String> list_webs;
    @FXML
    private Label lb_total_pages;
    @FXML
    private Label lb_processed;
    @FXML
    private Label lb_total_links;
    @FXML
    private ListView<String> list_links;

    AtomicInteger totalpages;
    AtomicInteger processed;
    AtomicInteger totallinks;
    ThreadPoolExecutor executor;

    private ScheduledService<Boolean> service;

    /**
     * *
     * This function initializes the application set
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        totalpages = new AtomicInteger(0);
        processed = new AtomicInteger(0);
        totallinks = new AtomicInteger(0);
        currentLinks = FXCollections.observableArrayList();

        declareService();

    }

    /**
     * This function is used to declare ScheduledService
     */
    public void declareService() {
        service = new ScheduledService<Boolean>() {
            @Override
            protected Task<Boolean> createTask() {
                return new Task<Boolean>() {
                    @Override
                    protected Boolean call() throws Exception {
                        return executor.isTerminated();
                    }
                };
            }
        };

        service.setOnSucceeded(e -> {
            lb_total_links.setText(totallinks.get() + "");
            lb_processed.setText(processed.get() + "");
            lb_total_pages.setText(totalpages.get() + "");
            if (service.getValue()) {
                service.cancel();
            }
        });

        service.setDelay(Duration.seconds(1));
        service.setPeriod(Duration.seconds(1));
    }

    /**
     * This function loads the links that are in a text document
     *
     * @param event
     */
    @FXML
    private void LoadFile(ActionEvent event) {

        File tmp = locateFile(event);
        // Initialize the array
        webpages = (ArrayList<WebPage>) FileUtils.loadPages(tmp);
        //Initialize the Observable list
        currentWebpages = FXCollections.observableArrayList();
        totalpages.set(webpages.size());
        MessageUtils.showMessage(webpages.size() + " pages found");

        for (WebPage t : webpages) {
            currentWebpages.add(t.getWeb_page());
        }
        list_webs.setItems(currentWebpages);
        lb_total_pages.setText(currentWebpages.size() + "");
        selectEvent();
    }

    /**
     * Function used to give FileChooser performance
     *
     * @param event
     * @return
     */
    protected File locateFile(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Open File");
        File file = chooser.showOpenDialog(new Stage());
        return file;
    }

    /**
     * *
     * function that is activated when you press the run button, which executes
     * the threads and obtains the data from the lists
     *
     * @param event
     */
    @FXML
    private void startProcess(ActionEvent event) {
        if (webpages == null || webpages.size() == 0) {
            MessageUtils.showError("No URL list loaded");
        } else {
            //create the Calleble arraylist
            ArrayList<Callable<WebPage>> callables = new ArrayList<>();

            //Addings the new Callables
            for (WebPage t : webpages) {
                callables.add(getListPages(t));
            }

            executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(webpages.size());

            List<Future<WebPage>> futList = callables.stream()
                    .map(c -> executor.submit(c))
                    .collect(Collectors.toList());

            executor.shutdown(); //wait that executor finish
            //calling the service
            service.restart();

        }

    }

    /**
     * This method returns a Callable<WebPage> with the list of updated links
     *
     * @param d
     * @return
     */
    public Callable<WebPage> getListPages(WebPage d) {
        return () -> { // Callable

            d.setList_pages(LinkReader.getLinks(d.getUrl_page()));

            totallinks.addAndGet(d.getList_pages().size());
            processed.addAndGet(1);
            return d;
        };
    }

    /**
     * In this function, we look for the item with the same name from the list
     *
     * @param nombre
     * @return
     */
    public List<String> getListByName(String name) {

        List<WebPage> to
                = webpages.stream().filter(t -> t.getWeb_page().equals(name))
                        .collect(Collectors.toList());
        if (!to.isEmpty()) {
            return to.get(0).getList_pages();
        }
        return null;
    }

    /**
     * Adding a listener for each of the elements in the list
     */
    public void selectEvent() {
        list_webs.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                    List<String> value = getListByName(newValue);
                    if (value != null) {
                        currentLinks.clear();
                        currentLinks.addAll(value);
                        list_links.setItems(currentLinks);
                    }
                });
    }

    /**
     * close the application from the menu option
     *
     * @param event
     */
    @FXML
    private void CloseApp(ActionEvent event) {
        Platform.exit();
    }

    /**
     *we clean all the graphic elements of the app
     * @param event
     */
    @FXML
    private void clearProcess(ActionEvent event) {
        // service.
        webpages = new ArrayList<>();
        currentLinks.clear();
        currentWebpages.clear();
        totallinks.set(0);
        totalpages.set(0);
        processed.set(0);

        lb_total_links.setText(totallinks.get() + "");
        lb_processed.setText(processed.get() + "");
        lb_total_pages.setText(totalpages.get() + "");
    }

}
