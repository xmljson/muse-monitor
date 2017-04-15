package com.codingbash.musemonitor.socketserver.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.codingbash.musemonitor.socketserver.model.InboundPayload;
import com.codingbash.musemonitor.socketserver.model.MentalStatus;
import com.codingbash.musemonitor.socketserver.model.OutboundIndicatorPayload;
import com.codingbash.musemonitor.socketserver.model.PhysicalStatus;
import com.codingbash.musemonitor.socketserver.model.PreviousStatusHolder;
import com.codingbash.musemonitor.socketserver.processor.FallDeterminationProcessor;
import com.google.gson.Gson;

@Component
public class OutboundIndicatorPayloadMapper {

	private final static Logger LOG = LoggerFactory.getLogger(OutboundIndicatorPayloadMapper.class);

	@Autowired
	private PreviousStatusHolder previousStatus;

	public OutboundIndicatorPayload mapOutboundIndicatorPayload(InboundPayload inboundPayload, boolean fallFlag,
			boolean seizureFlag) {
		OutboundIndicatorPayload outboundIndicatorPayload = new OutboundIndicatorPayload();
		outboundIndicatorPayload.setPatientId(inboundPayload.getPatientId());
		boolean newStatus = false;
		if (fallFlag) {
			outboundIndicatorPayload.setPhysicalStatus(PhysicalStatus.EMERGENCY);
			if (previousStatus.getPhysicalStatus() != PhysicalStatus.EMERGENCY) {
				// New status
				newStatus = true;
				previousStatus.setPhysicalStatus(PhysicalStatus.EMERGENCY);
			}
		} else {
			outboundIndicatorPayload.setPhysicalStatus(PhysicalStatus.GOOD);
			if (previousStatus.getPhysicalStatus() != PhysicalStatus.GOOD) {
				// New status
				newStatus = true;
				previousStatus.setPhysicalStatus(PhysicalStatus.GOOD);
			}
		}

		if (seizureFlag) {
			outboundIndicatorPayload.setMentalStatus(MentalStatus.EMERGENCY);
			if (previousStatus.getMentalStatus() != MentalStatus.EMERGENCY) {
				// New status
				newStatus = true;
				previousStatus.setMentalStatus(MentalStatus.EMERGENCY);
			}
		} else {
			outboundIndicatorPayload.setMentalStatus(MentalStatus.GOOD);
			if (previousStatus.getMentalStatus() != MentalStatus.GOOD) {
				// New status
				newStatus = true;
				previousStatus.setMentalStatus(MentalStatus.GOOD);
			}
		}

		if (newStatus) {
			outboundIndicatorPayload.setTimeMillis(inboundPayload.getTimeMills());
		}
		return outboundIndicatorPayload;
	}
}