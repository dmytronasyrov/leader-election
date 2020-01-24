package leader;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class LeaderElection implements Watcher {

  // Constants

  private static final String ZOOKEEPER_ADDRESS = "localhost:2181";
  private static final int SESSION_TIMEOUT = 3000;

  // Variables

  private ZooKeeper mZooKeeper;

  // Main

  public static void main(String[] args) throws IOException, InterruptedException {
    final LeaderElection leaderElection = new LeaderElection();
    leaderElection.connectToZookeeper();
    leaderElection.run();
    leaderElection.close();

    System.out.println("Disconnected from Zookeeper");
  }

  // Overrides

  @Override
  public void process(WatchedEvent watchedEvent) {
    switch (watchedEvent.getType()) {
      case None:
        if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
          System.out.println("Successfully connected to Zookeeper");
        } else {
          synchronized (mZooKeeper) {
            System.out.println("Disconnected from Zookeeper event");

            mZooKeeper.notifyAll();
          }
        }
    }
  }

  // Private

  private void connectToZookeeper() throws IOException {
    mZooKeeper = new ZooKeeper(ZOOKEEPER_ADDRESS, SESSION_TIMEOUT, this);
  }

  private void run() throws InterruptedException {
    synchronized (mZooKeeper) {
      mZooKeeper.wait();
    }
  }

  private void close() throws InterruptedException {
    mZooKeeper.close();
  }
}
