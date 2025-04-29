/**
 * This is the `EndBar` component, which represents the end bar of your application.
 * It typically contains information about the PathPerfect team and potentially other
 * content as well.
 *
 * @returns {JSX.Element} The JSX element representing the `EndBar` component.
 */
const EndBar = () => {
    const centerText = {
      display: "flex",
      alignItems: "center",
      justifyContent: "center",
    };
  
    return (
      <header className="w-full flex justify-between items-center flex-col endbar">
        <nav className="flex justify-between items-center w-full mb-10 pt-3">
          <p className="w-28" style={{ padding: "15px", marginLeft: "15px", fontSize: "20px", ...centerText }}>
            <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css"></link>
            <i className="fa fa-circle" style={{fontSize: "20px", color: "#0096FF"}}></i>
            &nbsp; PathPerfect Team
          </p>
        </nav>
      </header>
    );
  }
  
  export default EndBar;
  