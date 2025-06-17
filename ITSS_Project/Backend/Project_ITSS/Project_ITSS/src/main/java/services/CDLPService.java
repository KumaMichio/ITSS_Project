package services;

import exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import models.CDLP;
import org.springframework.stereotype.Service;
import repositories.CDLPRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class CDLPService {
    private final CDLPRepository cdlpRepository;

    public void addCDLPs(CDLP cdlp) {
        cdlpRepository.customInsert(
                cdlp.getArtist(),
                cdlp.getRecordLabel(),
                cdlp.getTrackList()
        );
    }

    public List<CDLP> getCDLPByTitleContaining(String title) {
        return cdlpRepository.findByTitleContaining(title);
    }

    public CDLP getCDLP(int id) {
        return cdlpRepository.findById(id).orElse(null);
    }

    public List<CDLP> getAllCDLPs() {
        return cdlpRepository.findAll();
    }

    public CDLP updateCDLP(int id, CDLP cdlpDetails) {
        CDLP cdlp = cdlpRepository.findById(id).orElse(null);
        if (cdlp != null) {
            cdlp.setArtist(cdlpDetails.getArtist());
            cdlp.setRecordLabel(cdlpDetails.getRecordLabel());
            cdlp.setTrackList(cdlpDetails.getTrackList());
            return cdlpRepository.save(cdlp);
        }
        else {
            throw new ResourceNotFoundException("CDLP not found with id " + id);
        }
    }

    public void deleteCDLP(int id) {
        cdlpRepository.deleteById(id);
    }

    public void deleteAllCDLPs() {
        cdlpRepository.deleteAll();
    }

}
