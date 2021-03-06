package org.mmmr.services.impl;

import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;

import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import org.mmmr.services.ExceptionAndLogHandler;
import org.mmmr.services.Messages;
import org.mmmr.services.interfaces.DownloadingServiceI;

/**
 * {@link URL}, {@link URLConnection} download service
 * 
 * @author Jurgen
 */
public class DownloadingServiceSimple implements DownloadingServiceI {
    public class DownloadProgressMonitor extends ProgressMonitor implements PropertyChangeListener {
        private final SwingWorker<String, Void> sw;

        public DownloadProgressMonitor(final Component parentComponent, final Object message, final String note, final OutputStream target,
                final URLConnection entity) {
            super(parentComponent, message, note, 0, 100);

            this.sw = new SwingWorker<String, Void>() {
                @Override
                public String doInBackground() {
                    this.setProgress(0);

                    try {
                        byte[] buffer = new byte[1024 * 8 * 4];
                        long dl = 0;
                        InputStream uin = entity.getInputStream();
                        int read;
                        while ((read = uin.read(buffer)) != -1) {
                            target.write(buffer, 0, read);
                            dl += read;
                            int percentage = (int) ((dl * 100l) / entity.getContentLength());
                            this.setProgress(percentage);
                        }
                        target.close();
                        uin.close();
                    } catch (Exception ex) {
                        return String.valueOf(ex);
                    }
                    return note;
                }

                @Override
                public void done() {
                    DownloadProgressMonitor.this.close();
                    // System.out.println("done");
                }
            };

            this.sw.addPropertyChangeListener(this);
            this.sw.execute();
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if ("progress" == evt.getPropertyName()) { //$NON-NLS-1$
                int progress = (Integer) evt.getNewValue();
                this.setProgress(progress);
                String message = String.format(Messages.getString("DownloadingServiceSimple.completed") + " %d%%.", progress); //$NON-NLS-1$ //$NON-NLS-2$
                this.setNote(message);
                // System.out.println(message);
                if (this.isCanceled() || this.sw.isDone()) {
                    if (this.isCanceled()) {
                        this.sw.cancel(true);
                        // System.out.println("Task canceled.");
                    } else {
                        // System.out.println("Task completed.");
                    }
                }
            }
        }
    }

    /**
     * 
     * @see org.mmmr.services.interfaces.DownloadingServiceI#downloadURL(java.net.URL)
     */
    @Override
    public byte[] downloadURL(URL url) throws IOException {
        ByteArrayOutputStream target = new ByteArrayOutputStream();
        this.downloadURL(url, target);
        return target.toByteArray();
    }

    /**
     * 
     * @see org.mmmr.services.interfaces.DownloadingServiceI#downloadURL(java.net.URL, java.io.File)
     */
    @Override
    public void downloadURL(URL url, File target) throws IOException {
        this.downloadURL(url, new FileOutputStream(target));
    }

    /**
     * 
     * @see org.mmmr.services.interfaces.DownloadingServiceI#downloadURL(java.net.URL, java.io.OutputStream)
     */
    @Override
    public void downloadURL(URL url, OutputStream target) throws IOException {
        ExceptionAndLogHandler.log(url);
        URLConnection conn = url.openConnection();
        conn.setAllowUserInteraction(false);
        conn.setConnectTimeout(60 * 1000);
        conn.setDefaultUseCaches(true);
        conn.setReadTimeout(60 * 1000);
        conn.setUseCaches(true);
        try {
            System.out.println(new DownloadProgressMonitor(null,
                    Messages.getString("DownloadingServiceSimple.downloading"), String.valueOf(url), target, conn).sw.get()); //$NON-NLS-1$
        } catch (InterruptedException ex) {
            //
        } catch (ExecutionException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * 
     * @see org.mmmr.services.interfaces.DownloadingServiceI#exists(java.lang.String)
     */
    @Override
    public boolean exists(String url) {
        return true;
    }

    /**
     * 
     * @see org.mmmr.services.interfaces.DownloadingServiceI#trace(java.net.URL)
     */
    @Override
    public String trace(URL url) throws IOException {
        throw new UnsupportedOperationException("trace: " + url); //$NON-NLS-1$
    }
}
