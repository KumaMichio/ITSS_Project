package services;

import exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import models.DVD;
import org.springframework.stereotype.Service;
import repositories.DVDRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class DVDService {
    private final DVDRepository dvdRepository;

    public void addDVD(DVD dvd) {
        dvdRepository.customInsert(
                dvd.getDiscType(),
                dvd.getDirector(),
                dvd.getRuntime(),
                dvd.getStudio(),
                dvd.getLanguage(),
                dvd.getSubtitles(),
                dvd.getReleaseDate().toString());
    }

    public List<DVD> getDVDByTitleContaining(String title) {
        return dvdRepository.findByTitleContaining(title);
    }

    public DVD getDVDById(int id) {
        return dvdRepository.findById(id).orElse(null);
    }

    public List<DVD> getAllDVDs() {
        return dvdRepository.findAll();
    }

    public DVD updateDVD(int id, DVD dvdDetails) {
        DVD dvd = dvdRepository.findById(id).orElse(null);
        if (dvd != null) {
            dvd.setDiscType(dvdDetails.getDiscType());
            dvd.setDirector(dvdDetails.getDirector());
            dvd.setRuntime(dvdDetails.getRuntime());
            dvd.setStudio(dvdDetails.getStudio());
            dvd.setLanguage(dvdDetails.getLanguage());
            dvd.setSubtitles(dvdDetails.getSubtitles());
            dvd.setReleaseDate(dvdDetails.getReleaseDate());
            return dvdRepository.save(dvd);
        }
        else {
            throw new ResourceNotFoundException("DVD not found with id " + id);
        }
    }

    public void deleteDVD(int id) {
        dvdRepository.deleteById(id);
    }
}
