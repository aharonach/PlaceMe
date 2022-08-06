import React, {useEffect} from 'react';
import {useParams, useNavigate} from "react-router-dom";
import useAxios from "../../hooks/useAxios";
import Api from "../../api";
import Loading from "../Loading";
import {Alert, Button, Col, Row} from "react-bootstrap";
import EditPupil from './EditPupil';
import EditGroups from "./EditGroups";

export default function PupilPage() {
    let { pupilId } = useParams();
    const [pupil, error, loading, axiosFetch] = useAxios();
    let navigate = useNavigate();

    const getPupil = () => {
        axiosFetch({
            axiosInstance: Api,
            method: 'get',
            url: `/pupils/${pupilId}`,
        });
    }

    const handleDelete = () => {
        axiosFetch({
            axiosInstance: Api,
            method: 'delete',
            url: `/pupils/${pupilId}`,
        }).then(() => navigate('/pupils', { replace: true }));
    }

    useEffect(() => {
        getPupil();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    return (
        <>
            {loading && <Loading />}
            {!loading && error && <Alert variant="danger">{error}</Alert>}
            {!loading && !error && pupil &&
                <article className="pupil">
                    <h2>{pupil.firstName} {pupil.lastName}</h2>
                    <Button variant="danger" onClick={handleDelete}>Delete Pupil</Button>
                    <Row className="mt-3">
                        <Col md={6}>
                            <EditPupil pupil={pupil} />
                        </Col>
                        <Col md={6}>
                            <EditGroups pupil={pupil} />
                        </Col>
                    </Row>
                </article>

            }
        </>
    )
}
