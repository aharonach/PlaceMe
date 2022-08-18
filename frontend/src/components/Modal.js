import React from 'react';
import {Modal as BSModal} from 'react-bootstrap';
import {useNavigate} from "react-router-dom";

export default function Modal({title, children}) {
    const navigate = useNavigate();
    const handleClose = () => navigate(-1);

    return (
        <BSModal show={true} onHide={handleClose}>
            <BSModal.Header closeButton>
                <BSModal.Title>{title}</BSModal.Title>
            </BSModal.Header>
            <BSModal.Body>{children}</BSModal.Body>
        </BSModal>
    )
}