import React, {useEffect} from 'react';
import {useParams, useNavigate} from "react-router-dom";
import useAxios from "../../hooks/useAxios";
import Api from "../../api";
import Loading from "../Loading";
import {Alert, Button} from "react-bootstrap";
import EditPupil from './EditPupil';

function PupilProfile() {
    let { pupilId } = useParams();
    const [pupil, error, loading, axiosFetch] = useAxios();
    let navigate = useNavigate();

    const getData = () => {
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
        });
        !error && navigate('/pupils', { replace: true });
    }

    // eslint-disable-next-line react-hooks/exhaustive-deps
    useEffect(() => getData(), []);

    return (
        <>
            {loading && <Loading />}
            {!loading && error && <Alert variant="danger">{error}</Alert>}
            {!loading && !error && pupil &&
                <article className="pupil">
                    <h2>{pupil.firstName} {pupil.lastName}</h2>
                    <Button variant="danger" onClick={handleDelete}>Delete</Button>
                    <EditPupil pupil={pupil} />
                </article>
            }
        </>
    )
}

export default PupilProfile;