import React, {useEffect} from 'react';
import {useParams, useNavigate} from "react-router-dom";
import useAxios from "../../hooks/useAxios";
import Api from "../../api";
import Loading from "../Loading";
import {Alert, Button} from "react-bootstrap";
import EditPupil from './EditPupil';
import Groups from "./Groups";

function PupilProfile() {
    let { pupilId } = useParams();
    const [pupil, error, loading, axiosFetch] = useAxios();
    const [groups, errorGroups, loadingGroups, axiosFetchGroups] = useAxios();
    let navigate = useNavigate();

    const getPupil = () => {
        axiosFetch({
            axiosInstance: Api,
            method: 'get',
            url: `/pupils/${pupilId}`,
        });
    }

    const getGroups = () => {
        axiosFetchGroups({
            axiosInstance: Api,
            method: 'get',
            url: `/pupils/${pupilId}/groups`,
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

    useEffect(() => {
        getPupil();
        getGroups();
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
                    {loadingGroups && <Loading />}
                    {!loadingGroups && errorGroups && <Alert variant="danger">{errorGroups}</Alert>}
                    {!loadingGroups && !errorGroups && groups && <Groups pupilGroups={groups} />}
                </article>
            }
        </>
    )
}

export default PupilProfile;