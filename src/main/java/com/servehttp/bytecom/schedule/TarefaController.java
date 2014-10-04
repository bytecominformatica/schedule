package com.servehttp.bytecom.schedule;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import com.servehttp.bytecom.schedule.ejb.MailEJB;
import com.servehttp.bytecom.schedule.util.DateUtil;

@Singleton
@Startup
public class TarefaController {

  private static final Logger LOGGER = Logger.getLogger(TarefaController.class.getName());
  private static final String DESTINATARIO = "clairton.c.l@gmail.com";
  @EJB
  private MailEJB mail;

  @Schedule(dayOfWeek = "Sun - Sat", hour = "3", persistent = false)
  public void backup() {
    LOGGER.info("[EFETUANDO BACKUP]");

    executar("/opt/script/./backup.sh");
    
    LOGGER.info("[BACKUP FINALIZADO]");
    try {
      Thread.sleep(1000 * 60 * 10);
    } catch (InterruptedException e) {
      LOGGER.log(Level.SEVERE, null, e);
    }
    LOGGER.info("[ENVIANDO BACKUP]");
    enviarBackupPorEmail();
    LOGGER.info("[BACKUP ENVIADO]");
  }

  private void executar(String command) {
    try {
      Runtime.getRuntime().exec(command);
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, null, e);
    }
  }

  private void enviarBackupPorEmail() {
    String dataBackup = DateUtil.INSTANCE.formataAnoMesDia(new Date());
    String assunto = "Backup " + dataBackup;
    String fileName = "bytecom" + dataBackup + ".sql";
    String file = "/opt/backup/" + fileName;

    mail.sendAttachment(DESTINATARIO, assunto, "", file, fileName);
  }

}
