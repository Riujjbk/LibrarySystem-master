package com.library.service;

import com.library.bean.ReaderInfo;
import com.library.dao.ReaderInfoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReaderInfoService {
    @Autowired
    private ReaderInfoDao readerInfoDao;

    public List<ReaderInfo> readerInfos() {
        return readerInfoDao.getAllReaderInfo();
    }

    public boolean deleteReaderInfo(long readerId) {
        return readerInfoDao.deleteById(readerId) > 0;
    }

    public ReaderInfo getReaderInfo(long readerId) {
        return readerInfoDao.selectById(readerId);
    }

    public boolean editReaderInfo(ReaderInfo readerInfo) {
        return readerInfoDao.updateById(readerInfo) > 0;
    }

    public boolean editReaderCard(ReaderInfo readerInfo) {
        // This method might not be needed anymore with MyBatis Plus
        return true; // Placeholder implementation
    }

    public boolean addReaderInfo(ReaderInfo readerInfo) {
        return readerInfoDao.addReaderInfo(readerInfo) > 0;
    }
}
