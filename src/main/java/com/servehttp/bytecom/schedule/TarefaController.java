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

    if (executar("/opt/script/./backup.sh")) {
      LOGGER.info("[ENVIANDO BACKUP]");
      enviarBackupPorEmail();
      LOGGER.info("[BACKUP ENVIADO]");
    } else {
      LOGGER.severe("[FALHA AO TENTAR FAZER O BACKUP]");
    }

    LOGGER.info("[BACKUP FINALIZADO]");
  }

  private boolean executar(String command) {
    boolean sucesso = false;
    try {
      Process processRuntime = Runtime.getRuntime().exec(command);
      int waitFor = processRuntime.waitFor();
      sucesso = waitFor == 0;
    } catch (IOException e) {
      LOGGER.log(Level.SEVERE, null, e);
      sendEmailFalha(e);
    } catch (InterruptedException e) {
      LOGGER.log(Level.SEVERE, null, e);
      sendEmailFalha(e);
    }
    return sucesso;
  }

  private void sendEmailFalha(Exception e) {
    mail.send(DESTINATARIO, "FALHA AO TENTAR EFETUAR O BACKUP", e.toString());
  }

  private void enviarBackupPorEmail() {
    String dataBackup = DateUtil.INSTANCE.formataAnoMesDia(new Date());
    String assunto = "Backup " + dataBackup;
    String fileName = "bytecom" + dataBackup + ".sql";
    String file = "/opt/backup/" + fileName;

    mail.sendAttachment(DESTINATARIO, assunto, "", file, fileName);
  }

}
