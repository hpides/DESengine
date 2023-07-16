import os
import plotly.graph_objects as go
import plotly.io as pio
import plotly.express as px
import os
import sys
# Append parent directory to import path
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
import config
pio.kaleido.scope.mathjax = None


fig = go.Figure()

fig.add_trace(go.Scatter(name="Central", x=[1, 2, 3, 4, 5, 6, 7, 8], mode='lines+markers'
                         , y=[1.49*1024*1024*1024, 2.98*1024*1024*1024, 4.47*1024*1024*1024, 5.96*1024*1024*1024, 7.45*1024*1024*1024, 8.94*1024*1024*1024, 10.43*1024*1024*1024, 11.92*1024*1024*1024]
                         , line=dict(dash='dash', color=config.central, width=5), marker=dict(size=20, symbol='circle')))
fig.add_trace(go.Scatter(name="Disco", x=[1, 2, 3, 4, 5, 6, 7, 8], mode='lines+markers'
                         , y=[1.675*1024*1024*1024, 3.35*1024*1024*1024, 5.025*1024*1024*1024, 6.7*1024*1024*1024, 8.375*1024*1024*1024, 10.05*1024*1024*1024, 11.725*1024*1024*1024, 13.4*1024*1024*1024]
                         , line=dict(dash='dashdot',color=config.disco, width=5), marker=dict(size=20, symbol='square')))
fig.add_trace(go.Scatter(name="Scotty", x=[1, 2, 3, 4, 5, 6, 7, 8], mode='lines+markers'
                         , y=[1.49*1024*1024*1024, 2.98*1024*1024*1024, 4.47*1024*1024*1024, 5.96*1024*1024*1024, 7.45*1024*1024*1024, 8.94*1024*1024*1024, 10.43*1024*1024*1024, 11.92*1024*1024*1024]
                         , line=dict(dash='dot', color=config.scotty, width=5), marker=dict(size=20, symbol='triangle-up')))
fig.add_trace(go.Scatter(name="Deco<sub>async</sub>", x=[1, 2, 3, 4, 5, 6, 7, 8], mode='lines+markers'
                         , y=[11.328*1024, 61.49*1024*1024, 92.23*1024*1024, 122.98*1024*1024, 153.73*1024*1024, 184.47*1024*1024, 215.22*1024*1024, 245.97*1024*1024]
                         , line=dict(color=config.decoasy, width=5), marker=dict(size=20, symbol='cross')))

# fig.update_traces(marker_color='rgb(158,202,225)', marker_line_color='rgb(8,48,107)', marker_line_width=1.5, opacity=0.6,
# marker=dict(size=10, symbol='triangle-up'))))

#legend
fig.update_layout(
    legend=dict(
        orientation="h",
        yanchor="top",
        y=1.2,
        xanchor="left",
        x=-0.05,
        # bordercolor="Black",
        # borderwidth=2,
        # bgcolor="white",
        font=dict(
            size=30,
            color="black"
        ),
    ),
    yaxis=dict(
        title_text="bytes sent",
        titlefont=dict(size=35),
        ticktext=["0", "KB", "MB", "GB"],
        tickvals=[0, 1000, 1000000, 1000000000],
        range=[0, 11],
        type="log",
        ticks = "inside",
        ticklen = 20,
        tickwidth = 5,
        tickfont=dict(size=35),
        # ticks="inside",
        # ticklen=20,
        # tickwidth =5,
    ),

    xaxis=dict(
        title_text="Local Nodes",
        titlefont=dict(size=35),
        ticktext=["1", "2", "3", "4", "5", "6", "7", "8"],
        tickvals=[1, 2, 3, 4, 5, 6, 7, 8],
        range=[1,8.2],
        tickmode="array",
        ticks="inside",
        ticklen=20,
        tickwidth=5,
        tickfont=dict(size=35),
    )

)

# size & color
fig.update_layout(
    autosize=False,
    width=660,
    height=440,
    paper_bgcolor='rgba(0,0,0,0)',
    plot_bgcolor='rgba(0,0,0,0)',
    margin=dict(
        l=5,
        r=5,
        b=5,
        t=5,
        pad=0
    ),
)
fig.update_xaxes(showline=True, linewidth=3, linecolor='black'#, mirror=True
                 )
fig.update_yaxes(showline=True, linewidth=3, linecolor='black'#, mirror=True
                 )
fig.update_yaxes(showgrid=True, gridwidth=1, gridcolor='rgb(120,120,120)')

fig.show()
# if not os.path.exists("E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s1"):
#     os.mkdir("E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s1")
# fig.write_image("images/fig1.svg")
pio.write_image(fig, "E:\On going Paper\Deco Efficient Decentralized Aggregation for Count-Based Windows\experiment\s1\/networkoverhead\/networkutilizationmanynodes.pdf")
pio.write_image(fig, "E:\On going Paper\Deco Efficient Decentralized Aggregation for Count-Based Windows\experiment\s1\/networkoverhead\/networkutilizationmanynodes.svg")
