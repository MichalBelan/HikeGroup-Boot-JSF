package uim.fei.stuba.sk.service;

import org.springframework.data.domain.Page;
import uim.fei.stuba.sk.dto.ClubDto;
import uim.fei.stuba.sk.model.Club;
import java.util.List;

public interface ClubService {
    List<ClubDto> findAllClubs();
    Club saveClub(ClubDto clubDto);
    ClubDto findClubById(long clubId);
    void updateClub(ClubDto club);
    void delete(Long clubId);
    List<ClubDto> searchClubs(String query);
    Page<ClubDto> findAllClubs(int page, int size);
}
