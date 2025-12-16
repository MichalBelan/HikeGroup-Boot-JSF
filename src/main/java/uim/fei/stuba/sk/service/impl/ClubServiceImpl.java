package uim.fei.stuba.sk.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uim.fei.stuba.sk.dto.ClubDto;
import uim.fei.stuba.sk.model.Club;
import uim.fei.stuba.sk.model.UserEntity;
import uim.fei.stuba.sk.repository.ClubRepository;
import uim.fei.stuba.sk.repository.UserRepository;
import uim.fei.stuba.sk.security.SecurityUtil;
import uim.fei.stuba.sk.service.ClubService;
import uim.fei.stuba.sk.service.EventService;
import java.util.List;
import java.util.stream.Collectors;
import static uim.fei.stuba.sk.mapper.ClubMapper.mapToClub;
import static uim.fei.stuba.sk.mapper.ClubMapper.mapToClubDto;

@Service
public class ClubServiceImpl implements ClubService {
    private ClubRepository clubRepository;
    private UserRepository userRepository;
    @Autowired
    private EventService eventService;


    @Autowired
    public ClubServiceImpl(ClubRepository clubRepository, UserRepository userRepository) {
        this.clubRepository = clubRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<ClubDto> findAllClubs() {
        List<Club> clubs = clubRepository.findAll();
        return clubs.stream().map((club -> mapToClubDto(club))).collect(Collectors.toList());
    }

    @Override
    public Club saveClub(ClubDto clubDto) {
        String username= SecurityUtil.getSessionUser();
        UserEntity user = userRepository.findByUsername(username);
        Club club = mapToClub(clubDto);
        club.setCreatedBy(user);
        return clubRepository.save(club);
    }

    @Override
    public ClubDto findClubById(long clubId) {
        Club club = clubRepository.findById(clubId).get();
        return mapToClubDto(club);
    }

    @Override
    public void updateClub(ClubDto clubDto) {
        String username= SecurityUtil.getSessionUser();
        UserEntity user = userRepository.findByUsername(username);
        Club club = mapToClub(clubDto);
        club.setCreatedBy(user);
        clubRepository.save(club);

    }

    @Override
    public void delete(Long clubId) {
        clubRepository.deleteById(clubId);
    }

    @Override
    public List<ClubDto> searchClubs(String query) {
        List<Club> clubs = clubRepository.searchClubs(query);
        return clubs.stream().map((club -> mapToClubDto(club))).collect(Collectors.toList());
    }

    @Override
    public Page<ClubDto> findAllClubs(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Club> clubsPage = clubRepository.findAll(pageable);
        return clubsPage.map(club -> mapToClubDto(club));
    }


}

