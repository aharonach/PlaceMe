import React from 'react';
import {useParams, useNavigate} from "react-router-dom";
import useFetchRecord from "../../hooks/useFetchRecord";
import Api from "../../api";
import Loading from "../Loading";
import {Alert, Button} from "react-bootstrap";
import EditGroups from "./EditGroups";
import RecordDetails from "../RecordDetails";
import {LinkContainer} from "react-router-bootstrap";

export default function PupilPage() {
    let { pupilId } = useParams();
    const [pupil, error, loading, axiosFetch] = useFetchRecord(`/pupils/${pupilId}`);
    let navigate = useNavigate();

    const details = pupil && [
        { label: "Given ID", value: pupil.givenId },
        { label: "First Name", value: pupil.firstName },
        { label: "Last Name", value: pupil.lastName },
        { label: "Gender", value: pupil.gender },
        { label: "Birth Date", value: pupil.birthDate },
        { label: "Created Time", value: pupil.createdTime },
    ];

    const handleDelete = () => {
        axiosFetch({
            axiosInstance: Api,
            method: 'delete',
            url: `/pupils/${pupilId}`,
        }).then(() => navigate('/pupils', { replace: true }));
    }

    return (
        <>
            {loading && <Loading />}
            {!loading && error && <Alert variant="danger">{error}</Alert>}
            {!loading && !error && pupil &&
                <article className="pupil">
                    <h2>{pupil.firstName} {pupil.lastName}</h2>
                    <LinkContainer to="edit"><Button>Edit</Button></LinkContainer>
                    <Button variant="danger" onClick={handleDelete}>Delete Pupil</Button>
                    <RecordDetails numOfColumns={3} details={details} />
                    <EditGroups pupil={pupil} />
                </article>
            }
        </>
    )
}
