package com.cdg.springjwt;

import com.cdg.springjwt.controllers.MissionFullDTO;
import com.cdg.springjwt.models.Mission;
import com.cdg.springjwt.repository.MissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class MissionService {

    @Autowired
    private MissionRepository missionRepository;

    public Page<MissionFullDTO> getMissionsWithFilters(Specification<Mission> spec, Pageable pageable) {
        return missionRepository.findAll(spec, pageable).map(MissionFullDTO::from);
    }


}
