import React, {useEffect, useState} from 'react';
import {useParams, useNavigate} from "react-router-dom";
import useAxios from "../../hooks/useAxios";
import Api from "../../api";
import Loading from "../Loading";
import {Alert, Button} from "react-bootstrap";
import EditPupil from './EditPupil';
import EditGroups from "./EditGroups";

function PupilProfile() {
    let { pupilId } = useParams();
    const [pupil, error, loading, axiosFetch] = useAxios();
    const [deleted, setDeleted] = useState(false);
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
        });
        setDeleted(true);
    }

    useEffect(() => {
        getPupil();

        if ( deleted ) {
            navigate('/pupils', { replace: true });
        }
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
                    <EditPupil pupil={pupil} />

                    {/** Groups **/}
                    <EditGroups pupil={pupil} />
                </article>
            }
        </>
    )
}

export default PupilProfile;