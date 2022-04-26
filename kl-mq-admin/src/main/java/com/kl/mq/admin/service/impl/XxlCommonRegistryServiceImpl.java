package com.kl.mq.admin.service.impl;

import com.Kl.mq.admin.core.model.KlCommonRegistry;
import com.Kl.mq.admin.core.model.KlCommonRegistryData;
import com.Kl.mq.admin.core.model.KlCommonRegistryMessage;
import com.Kl.mq.admin.core.result.ReturnT;
import com.Kl.mq.admin.core.util.JacksonUtil;
import com.Kl.mq.admin.core.util.PropUtil;
import com.Kl.mq.admin.dao.IKlCommonRegistryDao;
import com.Kl.mq.admin.dao.IKlCommonRegistryDataDao;
import com.Kl.mq.admin.dao.IKlCommonRegistryMessageDao;
import com.Kl.mq.admin.service.KlCommonRegistryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;
import java.util.concurrent.*;

/**
 * Kl native regsitry, borrowed from "Kl-rpc"
 *
 * @author xuxueli 2018-11-26
 */
@Service
public class KlCommonRegistryServiceImpl implements KlCommonRegistryService, InitializingBean, DisposableBean {
    private static Logger logger = LoggerFactory.getLogger(KlCommonRegistryServiceImpl.class);


    @Resource
    private IKlCommonRegistryDao KlCommonRegistryDao;
    @Resource
    private IKlCommonRegistryDataDao KlCommonRegistryDataDao;
    @Resource
    private IKlCommonRegistryMessageDao KlCommonRegistryMessageDao;

    @Value("${Kl.mq.registry.data.filepath}")
    private String registryDataFilePath;
    @Value("${Kl.mq.registry.beattime}")
    private int registryBeatTime;
    @Value("${Kl.mq.registry.accessToken}")
    private String accessToken;

    @Override
    public ReturnT<String> registry(String accessToken, List<KlCommonRegistryData> KlCommonRegistryDataList) {
        // valid
        if (this.accessToken!=null && this.accessToken.trim().length()>0 && !this.accessToken.equals(accessToken)) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "AccessToken Invalid");
        }
        if (KlCommonRegistryDataList==null || KlCommonRegistryDataList.size()==0) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "RegistryData Invalid.");
        }
        for (KlCommonRegistryData registryData: KlCommonRegistryDataList) {
            if (registryData.getKey()==null || registryData.getKey().trim().length()==0 || registryData.getKey().trim().length()>255) {
                return new ReturnT<String>(ReturnT.FAIL_CODE, "RegistryData Key Invalid[0~255]");
            }
            if (registryData.getValue()==null || registryData.getValue().trim().length()==0 || registryData.getValue().trim().length()>255) {
                return new ReturnT<String>(ReturnT.FAIL_CODE, "RegistryData Value Invalid[0~255]");
            }
        }

        // add queue
        registryQueue.addAll(KlCommonRegistryDataList);

        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<String> remove(String accessToken, List<KlCommonRegistryData> KlCommonRegistryDataList) {
        // valid
        if (this.accessToken!=null && this.accessToken.trim().length()>0 && !this.accessToken.equals(accessToken)) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "AccessToken Invalid");
        }
        if (KlCommonRegistryDataList==null || KlCommonRegistryDataList.size()==0) {
            return new ReturnT<String>(ReturnT.FAIL_CODE, "RegistryData Invalid.");
        }
        for (KlCommonRegistryData registryData: KlCommonRegistryDataList) {
            if (registryData.getKey()==null || registryData.getKey().trim().length()==0 || registryData.getKey().trim().length()>255) {
                return new ReturnT<String>(ReturnT.FAIL_CODE, "RegistryData Key Invalid[0~255]");
            }
            if (registryData.getValue()==null || registryData.getValue().trim().length()==0 || registryData.getValue().trim().length()>255) {
                return new ReturnT<String>(ReturnT.FAIL_CODE, "RegistryData Value Invalid[0~255]");
            }
        }

        // add queue
        removeQueue.addAll(KlCommonRegistryDataList);

        return ReturnT.SUCCESS;
    }

    @Override
    public ReturnT<Map<String, List<String>>> discovery(String accessToken, List<String> keys) {
        // valid
        if (this.accessToken!=null && this.accessToken.trim().length()>0 && !this.accessToken.equals(accessToken)) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "AccessToken Invalid");
        }
        if (keys==null || keys.size()==0) {
            return new ReturnT<>(ReturnT.FAIL_CODE, "keys Invalid.");
        }
        for (String key: keys) {
            if (key==null || key.trim().length()==0 || key.trim().length()>255) {
                return new ReturnT<>(ReturnT.FAIL_CODE, "Key Invalid[0~255]");
            }
        }

        Map<String, List<String>> result = new HashMap<String, List<String>>();
        for (String key: keys) {
            KlCommonRegistryData KlCommonRegistryData = new KlCommonRegistryData();
            KlCommonRegistryData.setKey(key);

            List<String> dataList = new ArrayList<String>();
            KlCommonRegistry fileKlCommonRegistry = getFileRegistryData(KlCommonRegistryData);
            if (fileKlCommonRegistry!=null) {
                dataList = fileKlCommonRegistry.getDataList();
            }

            result.put(key, dataList);
        }

        return new ReturnT<Map<String, List<String>>>(result);
    }

    @Override
    public DeferredResult<ReturnT<String>> monitor(String accessToken, List<String> keys) {
        // init
        DeferredResult deferredResult = new DeferredResult(registryBeatTime * 3 * 1000L, new ReturnT<>(ReturnT.SUCCESS_CODE, "Monitor timeout."));

        // valid
        if (this.accessToken!=null && this.accessToken.trim().length()>0 && !this.accessToken.equals(accessToken)) {
            deferredResult.setResult(new ReturnT<>(ReturnT.FAIL_CODE, "AccessToken Invalid"));
            return deferredResult;
        }
        if (keys==null || keys.size()==0) {
            deferredResult.setResult(new ReturnT<>(ReturnT.FAIL_CODE, "keys Invalid."));
            return deferredResult;
        }
        for (String key: keys) {
            if (key==null || key.trim().length()==0 || key.trim().length()>255) {
                deferredResult.setResult(new ReturnT<>(ReturnT.FAIL_CODE, "Key Invalid[0~255]"));
                return deferredResult;
            }
        }

        // monitor by client
        for (String key: keys) {
            String fileName = parseRegistryDataFileName(key);

            List<DeferredResult> deferredResultList = registryDeferredResultMap.get(fileName);
            if (deferredResultList == null) {
                deferredResultList = new ArrayList<>();
                registryDeferredResultMap.put(fileName, deferredResultList);
            }

            deferredResultList.add(deferredResult);
        }

        return deferredResult;
    }

    /**
     * update Registry And Message
     */
    private void checkRegistryDataAndSendMessage(KlCommonRegistryData KlCommonRegistryData){
        // data json
        List<KlCommonRegistryData> KlCommonRegistryDataList = KlCommonRegistryDataDao.findData(KlCommonRegistryData.getKey());
        List<String> valueList = new ArrayList<>();
        if (KlCommonRegistryDataList!=null && KlCommonRegistryDataList.size()>0) {
            for (KlCommonRegistryData dataItem: KlCommonRegistryDataList) {
                valueList.add(dataItem.getValue());
            }
        }
        String dataJson = JacksonUtil.writeValueAsString(valueList);

        // update registry and message
        KlCommonRegistry KlCommonRegistry = KlCommonRegistryDao.load(KlCommonRegistryData.getKey());
        boolean needMessage = false;
        if (KlCommonRegistry == null) {
            KlCommonRegistry = new KlCommonRegistry();
            KlCommonRegistry.setKey(KlCommonRegistryData.getKey());
            KlCommonRegistry.setData(dataJson);
            KlCommonRegistryDao.add(KlCommonRegistry);
            needMessage = true;
        } else {
            if (!KlCommonRegistry.getData().equals(dataJson)) {
                KlCommonRegistry.setData(dataJson);
                KlCommonRegistryDao.update(KlCommonRegistry);
                needMessage = true;
            }
        }

        if (needMessage) {
            // sendRegistryDataUpdateMessage (registry update)
            sendRegistryDataUpdateMessage(KlCommonRegistry);
        }

    }

    /**
     * send RegistryData Update Message
     */
    private void sendRegistryDataUpdateMessage(KlCommonRegistry KlRpcRegistry){
        String registryUpdateJson = JacksonUtil.writeValueAsString(KlRpcRegistry);

        KlCommonRegistryMessage registryMessage = new KlCommonRegistryMessage();
        registryMessage.setData(registryUpdateJson);
        KlCommonRegistryMessageDao.add(registryMessage);
    }
    
    // ------------------------ broadcase + file data ------------------------

    private ExecutorService executorService = Executors.newCachedThreadPool();
    private volatile boolean executorStoped = false;
    private volatile List<Integer> readedMessageIds = Collections.synchronizedList(new ArrayList<Integer>());

    private volatile LinkedBlockingQueue<KlCommonRegistryData> registryQueue = new LinkedBlockingQueue<KlCommonRegistryData>();
    private volatile LinkedBlockingQueue<KlCommonRegistryData> removeQueue = new LinkedBlockingQueue<KlCommonRegistryData>();
    private Map<String, List<DeferredResult>> registryDeferredResultMap = new ConcurrentHashMap<>();

    public static KlCommonRegistryData staticRegistryData;

    @Override
    public void afterPropertiesSet() throws Exception {

        /**
         * registry registry data         (client-num/10 s)
         */
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                while (!executorStoped) {
                    try {
                        KlCommonRegistryData KlCommonRegistryData = registryQueue.take();
                        if (KlCommonRegistryData !=null) {

                            // refresh or add
                            int ret = KlCommonRegistryDataDao.refresh(KlCommonRegistryData);
                            if (ret == 0) {
                                KlCommonRegistryDataDao.add(KlCommonRegistryData);
                            }

                            // valid file status
                            KlCommonRegistry fileKlCommonRegistry = getFileRegistryData(KlCommonRegistryData);
                            if (fileKlCommonRegistry!=null && fileKlCommonRegistry.getDataList().contains(KlCommonRegistryData.getValue())) {
                                continue;     // "Repeated limited."
                            }

                            // checkRegistryDataAndSendMessage
                            checkRegistryDataAndSendMessage(KlCommonRegistryData);
                        }
                    } catch (Exception e) {
                        if (!executorStoped) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }
            }
        });

        /**
         * remove registry data         (client-num/start-interval s)
         */
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                while (!executorStoped) {
                    try {
                        KlCommonRegistryData KlCommonRegistryData = removeQueue.take();
                        if (KlCommonRegistryData != null) {

                            // delete
                            KlCommonRegistryDataDao.deleteDataValue(KlCommonRegistryData.getKey(), KlCommonRegistryData.getValue());

                            // valid file status
                            KlCommonRegistry fileKlCommonRegistry = getFileRegistryData(KlCommonRegistryData);
                            if (fileKlCommonRegistry!=null && !fileKlCommonRegistry.getDataList().contains(KlCommonRegistryData.getValue())) {
                                continue;   // "Repeated limited."
                            }

                            // checkRegistryDataAndSendMessage
                            checkRegistryDataAndSendMessage(KlCommonRegistryData);
                        }
                    } catch (Exception e) {
                        if (!executorStoped) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }
            }
        });

        /**
         * broadcase new one registry-data-file     (1/1s)
         *
         * clean old message   (1/10s)
         */
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                while (!executorStoped) {
                    try {
                        // new message, filter readed
                        List<KlCommonRegistryMessage> messageList = KlCommonRegistryMessageDao.findMessage(readedMessageIds);
                        if (messageList!=null && messageList.size()>0) {
                            for (KlCommonRegistryMessage message: messageList) {
                                readedMessageIds.add(message.getId());

                                // from registry、add、update、deelete，ne need sync from db, only write
                                KlCommonRegistry KlCommonRegistry = JacksonUtil.readValue(message.getData(), KlCommonRegistry.class);

                                // default, sync from db （aready sync before message, only write）

                                // sync file
                                setFileRegistryData(KlCommonRegistry);
                            }
                        }

                        // clean old message;
                        if (System.currentTimeMillis() % registryBeatTime ==0) {
                            KlCommonRegistryMessageDao.cleanMessage(10);
                            readedMessageIds.clear();
                        }
                    } catch (Exception e) {
                        if (!executorStoped) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (Exception e) {
                        if (!executorStoped) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }
            }
        });

        /**
         *  clean old registry-data     (1/10s)
         *
         *  sync total registry-data db + file      (1+N/10s)
         *
         *  clean old registry-data file
         */
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                while (!executorStoped) {
                    try {

                        // + static registry
                        if (staticRegistryData != null) {
                            registryQueue.add(staticRegistryData);
                        }

                        // clean old registry-data in db
                        KlCommonRegistryDataDao.cleanData(registryBeatTime * 3);

                        // + clean old registry in db
                        KlCommonRegistryDao.cleanDead();

                        // sync registry-data, db + file
                        int offset = 0;
                        int pagesize = 1000;
                        List<String> registryDataFileList = new ArrayList<>();

                        List<KlCommonRegistry> registryList = KlCommonRegistryDao.pageList(offset, pagesize);
                        while (registryList!=null && registryList.size()>0) {

                            for (KlCommonRegistry registryItem: registryList) {

                                // default, sync from db
                                List<KlCommonRegistryData> KlCommonRegistryDataList = KlCommonRegistryDataDao.findData(registryItem.getKey());
                                List<String> valueList = new ArrayList<String>();
                                if (KlCommonRegistryDataList!=null && KlCommonRegistryDataList.size()>0) {
                                    for (KlCommonRegistryData dataItem: KlCommonRegistryDataList) {
                                        valueList.add(dataItem.getValue());
                                    }
                                }
                                String dataJson = JacksonUtil.writeValueAsString(valueList);

                                // check update, sync db
                                if (!registryItem.getData().equals(dataJson)) {
                                    registryItem.setData(dataJson);
                                    KlCommonRegistryDao.update(registryItem);
                                }

                                // sync file
                                String registryDataFile = setFileRegistryData(registryItem);

                                // collect registryDataFile
                                registryDataFileList.add(registryDataFile);
                            }


                            offset += 1000;
                            registryList = KlCommonRegistryDao.pageList(offset, pagesize);
                        }

                        // clean old registry-data file
                        cleanFileRegistryData(registryDataFileList);

                    } catch (Exception e) {
                        if (!executorStoped) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                    try {
                        TimeUnit.SECONDS.sleep(registryBeatTime);
                    } catch (Exception e) {
                        if (!executorStoped) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }
            }
        });


    }

    @Override
    public void destroy() throws Exception {
        executorStoped = true;
        executorService.shutdownNow();
    }


    // ------------------------ file opt ------------------------

    // get
    public KlCommonRegistry getFileRegistryData(KlCommonRegistryData KlCommonRegistryData){

        // fileName
        String fileName = parseRegistryDataFileName(KlCommonRegistryData.getKey());

        // read
        Properties prop = PropUtil.loadProp(fileName);
        if (prop!=null) {
            KlCommonRegistry fileKlCommonRegistry = new KlCommonRegistry();
            fileKlCommonRegistry.setData(prop.getProperty("data"));
            fileKlCommonRegistry.setDataList(JacksonUtil.readValue(fileKlCommonRegistry.getData(), List.class));
            return fileKlCommonRegistry;
        }
        return null;
    }
    private String parseRegistryDataFileName(String key){
        // fileName
        String fileName = registryDataFilePath
                .concat(File.separator).concat(key)
                .concat(".properties");
        return fileName;
    }

    // set
    public String setFileRegistryData(KlCommonRegistry KlCommonRegistry){

        // fileName
        String fileName = parseRegistryDataFileName(KlCommonRegistry.getKey());

        // valid repeat update
        Properties existProp = PropUtil.loadProp(fileName);
        if (existProp != null && existProp.getProperty("data").equals(KlCommonRegistry.getData())
                ) {
            return new File(fileName).getPath();
        }

        // write
        Properties prop = new Properties();
        prop.setProperty("data", KlCommonRegistry.getData());

        PropUtil.writeProp(prop, fileName);

        logger.info(">>>>>>>>>>> Kl-mq, setFileRegistryData: key={}, data={}", KlCommonRegistry.getKey(), KlCommonRegistry.getData());


        // brocast monitor client
        List<DeferredResult> deferredResultList = registryDeferredResultMap.get(fileName);
        if (deferredResultList != null) {
            registryDeferredResultMap.remove(fileName);
            for (DeferredResult deferredResult: deferredResultList) {
                deferredResult.setResult(new ReturnT<>(ReturnT.SUCCESS_CODE, "Monitor key update."));
            }
        }

        return new File(fileName).getPath();
    }
    // clean
    public void cleanFileRegistryData(List<String> registryDataFileList){
        filterChildPath(new File(registryDataFilePath), registryDataFileList);
    }

    public void filterChildPath(File parentPath, final List<String> registryDataFileList){
        if (!parentPath.exists() || parentPath.list()==null || parentPath.list().length==0) {
            return;
        }
        File[] childFileList = parentPath.listFiles();
        for (File childFile: childFileList) {
            if (childFile.isFile() && !registryDataFileList.contains(childFile.getPath())) {
                childFile.delete();

                logger.info(">>>>>>>>>>> Kl-mq, cleanFileRegistryData, RegistryData Path={}", childFile.getPath());
            }
            if (childFile.isDirectory()) {
                if (parentPath.listFiles()!=null && parentPath.listFiles().length>0) {
                    filterChildPath(childFile, registryDataFileList);
                } else {
                    childFile.delete();
                }

            }
        }

    }


}
